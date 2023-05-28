package com.example.blackphototatoo;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EmailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_fragment);


        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);

        String userId1 = "pK9Qu9DHErU3Vi7BxMfSd31glYv1";
        String userId2 = "7UhGbChcmLTZYKT3ZmrESQd9xA93";
        String conversationId = generateConversationId(userId1, userId2);

        findViewById(R.id.btnSendChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el mensaje del campo de texto de entrada
                EditText messageEditText = findViewById(R.id.chatEditText);
                String message = messageEditText.getText().toString();

                // Crear un HashMap para almacenar los datos del mensaje
                Map<String, Object> chatMessage = new HashMap<>();
                chatMessage.put("content", message);
                chatMessage.put("timestamp", new Date());
                chatMessage.put("user", currentUser.getUid()); // Agregar el ID del usuario autenticado al mensaje

                // Obtener la referencia a la colección "chats" y al documento de la conversación específica
                CollectionReference chatsCollectionRef = db.collection("chats");
                DocumentReference conversationDocRef = chatsCollectionRef.document(conversationId);

                // Guardar el mensaje en Firestore
                conversationDocRef.collection("messages").add(chatMessage)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Éxito al guardar el mensaje
                                messageEditText.setText(""); // Limpiar el campo de texto de entrada
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al guardar el mensaje
                                Toast.makeText(EmailActivity.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                            }
                        });
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
                            String userId = document.getString("user");
                            // Mostrar solo los mensajes del usuario autenticado
                            if (userId != null && userId.equals(currentUser.getUid())) {

                            }
                            boolean isSender = userId != null && userId.equals(currentUser.getUid());

                            ChatMessage message = new ChatMessage(content, timestamp, userId, isSender);
                            messageList.add(message);

                        }

                        messageAdapter.notifyDataSetChanged();
                        messageRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            ChatMessage message = messageList.get(position);
            holder.bind(message);

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
                profileImageView = itemView.findViewById(R.id.tatooImageView);
            }

            public void bind(ChatMessage message) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String timestamp = dateFormat.format(message.getTimestamp());
                contentTextView.setText(message.getContent());
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) contentTextView.getLayoutParams();
                if (message.isSender()) {
                    timestampTextView.setText("\n" + "                                      " + timestamp + "\n");
                    layoutParams.setMarginStart(550); // Alinear a la derecha
                    contentTextView.setBackgroundResource(R.drawable.message_bubble_right);
                } else {
                    timestampTextView.setText("\n"+ timestamp + "\n");
                    layoutParams.setMarginStart(0); // Alinear a la izquierda
                    contentTextView.setBackgroundResource(R.drawable.message_bubble_left);
                }
                contentTextView.setLayoutParams(layoutParams);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference profileRef = storage.getReference().child("profileImages/").child("981f2c0f-6d4a-40de-9b34-0e55654932d2");

                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext())
                                .load(uri)
                                .into(profileImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejo de errores en caso de que no se pueda obtener la foto de perfil
                        // Puedes mostrar una imagen de perfil predeterminada o dejar el ImageView vacío
                    }
                });

            }
        }

//        findViewById(R.id.button13).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

}
