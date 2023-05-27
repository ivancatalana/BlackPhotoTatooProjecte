package com.example.blackphototatoo;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmailFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private EditText chatSend;
    private ImageButton btnSendOnChat;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;
    private boolean isSender;
    private String senderUid;
    private String receiverUid;
    private String conversationId;
    private String photoUrl;
    private String nameReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener los argumentos del fragmento
        Bundle arguments = getArguments();
        if (arguments != null) {
            senderUid = arguments.getString("senderUid");
            receiverUid = arguments.getString("receiverUid");
            nameReceiver = arguments.getString("name");
            photoUrl = arguments.getString("photoURL");
        }

        // Crear la ID de conversación combinando los IDs de los usuarios
        conversationId = generateConversationId(senderUid, receiverUid);

        System.out.println("----------------------------------------------------------------photo"+ photoUrl+"                   ------------ -- "+nameReceiver);
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Crear la colección de mensajes si no existe
        createMessagesCollectionIfNotExists(conversationId);

        // Resto del código del fragmento...
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView headerTextView = view.findViewById(R.id.headerTextView);
        headerTextView.setText(nameReceiver);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        chatSend = view.findViewById(R.id.chatEditText);
        btnSendOnChat = view.findViewById(R.id.btnSendChat);
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecyclerView.setAdapter(messageAdapter);


        String conversationId = generateConversationId(senderUid, receiverUid);

        btnSendOnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el mensaje del campo de texto de entrada


                String message = chatSend.getText().toString();
                //messageEditText.getText().toString();
                if(!message.isEmpty()){
                // Crear un HashMap para almacenar los datos del mensaje
                Map<String, Object> chatMessage = new HashMap<>();
                chatMessage.put("content", message);
                chatMessage.put("timestamp", new Date());
                chatMessage.put("sender", currentUser.getUid()); // Agregar el ID del usuario autenticado al mensaje
                chatMessage.put("receiver", receiverUid); // Agregar el ID del usuario destinatario del mensaje

                // Obtener la referencia a la colección "chats" y al documento de la conversación específica
                CollectionReference chatsCollectionRef = db.collection("chats");
                DocumentReference conversationDocRef = chatsCollectionRef.document(conversationId);

                // Guardar el mensaje en Firestore
                conversationDocRef.collection("messages").add(chatMessage)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Éxito al guardar el mensaje
                                chatSend.setText(""); // Limpiar el campo de texto de entrada
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al guardar el mensaje
                                Toast.makeText(getActivity(), "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                            }
                        });
                   }
            }
        });

        // Obtener los mensajes existentes de Firestore y mostrarlos en el RecyclerView
        CollectionReference chatsCollectionRef = db.collection("chats");
        DocumentReference conversationDocRef = chatsCollectionRef.document(conversationId);
        conversationDocRef.collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Manejar el error
                            return;
                        }

                        messageList.clear();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String content = document.getString("content");
                            Date timestamp = document.getDate("timestamp");
                            String userId = document.getString("sender");
                            String receiverId = document.getString("receiver");
                            // Mostrar solo los mensajes del usuario autenticado
                            if (userId != null && userId.equals(currentUser.getUid())) {
                                isSender = true;
                            }
                             isSender = userId != null && userId.equals(currentUser.getUid());

                            ChatMessage message = new ChatMessage(content, timestamp, userId, isSender);
                            messageList.add(message);

                        }

                        messageAdapter.notifyDataSetChanged();
                        messageRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });

        return view;
    }

    private String generateConversationId(String userId1, String userId2) {
        String sortedUid1 = userId1.compareTo(userId2) < 0 ? userId1 : userId2;
        String sortedUid2 = userId1.compareTo(userId2) < 0 ? userId2 : userId1;
        return sortedUid1 + sortedUid2;
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private List<ChatMessage> messageList;

        public MessageAdapter(List<ChatMessage> messageList) {
            this.messageList = messageList;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                // Inflar el diseño para mensaje enviado
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            } else {
                // Inflar el diseño para mensaje recibido
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            }
            return new MessageViewHolder(view);
        }
        @Override
        public int getItemViewType(int position) {
            ChatMessage message = messageList.get(position);
            if (message.isSender()) {
                // Mensaje enviado
                return 1;
            } else {
                // Mensaje recibido
                return 2;
            }
        }
        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            ChatMessage message = messageList.get(position);
            holder.bind(message );
        }


        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder {
            private TextView contentTextView;
            private TextView timestampTextView;
            private ImageView profileImageView;


            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                timestampTextView = itemView.findViewById(R.id.timestampTextView);
                profileImageView = itemView.findViewById(R.id.profileImageView);
            }

            public void bind(ChatMessage message) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String timestamp = dateFormat.format(message.getTimestamp());
                contentTextView.setText(message.getContent());
                timestampTextView.setText(timestamp);

                // Configurar la gravedad del texto y la hora según el remitente del mensaje
                if (message.isSender()) {
                    contentTextView.setGravity(Gravity.END); // Alinear a la derecha
                    //timestampTextView.setGravity(Gravity.END); // Alinear a la derecha
                    Glide.with(itemView.getContext()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).circleCrop().into(profileImageView);
                } else {
                    contentTextView.setGravity(Gravity.START); // Alinear a la izquierda
                    timestampTextView.setGravity(Gravity.START); // Alinear a la izquierda
                    Glide.with(itemView.getContext()).load(photoUrl).circleCrop().into(profileImageView);
                }
            }
        }
    }

    private void createMessagesCollectionIfNotExists(String conversationId) {
        db.collection("chats")
                .document(conversationId)
                .collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                            // La colección no existe o está vacía, así que la creamos
                            Map<String, Object> initialMessage = new HashMap<>();
                            initialMessage.put("content", "");
                            initialMessage.put("timestamp", new Date());
                            initialMessage.put("sender", senderUid);
                            initialMessage.put("receiver", receiverUid);

                            // Agregar un campo adicional al documento de "chats"
                            Map<String, Object> chatData = new HashMap<>();
                            chatData.put("conversationId", conversationId);
                            chatData.put("sender", senderUid);
                            chatData.put("receiver", receiverUid);

                            db.collection("chats")
                                    .document(conversationId)
                                    .set(chatData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("EmailFragment", "Documento de chat creado");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("EmailFragment", "Error al crear el documento de chat", e);
                                        }
                                    });

                            db.collection("chats")
                                    .document(conversationId)
                                    .collection("messages")
                                    .add(initialMessage)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("EmailFragment", "Colección de mensajes creada");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("EmailFragment", "Error al crear la colección de mensajes", e);
                                        }
                                    });
                        } else {
                            // La colección ya existe, no es necesario crearla
                            Log.d("EmailFragment", "La colección de mensajes ya existe");
                        }
                    }
                });
    }


}
