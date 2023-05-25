package com.example.blackphototatoo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatList extends Fragment {
    private RecyclerView recyclerView;
    private List<MyEmails>listaEmails=new ArrayList<>();
    private String tuUid;
    private String nameActualized;
    private String photoActualizerd;
    public ChatList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewchatList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fra, new EmailFragment())
//                        .addToBackStack(null)
//                        .commit();


            }
        });
        return view;
    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        List<MyEmails> myObjects = new ArrayList<>();
//        CollectionReference chatsCollectionRef = FirebaseFirestore.getInstance().collection("chats");
//        chatsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                myObjects.clear();  // Limpiar la lista antes de agregar nuevos elementos
//
//                List<DocumentReference> messageRefs = new ArrayList<>();  // Lista de referencias a los documentos de mensajes
//
//                for (QueryDocumentSnapshot chatDocument : querySnapshot) {
//                    CollectionReference messagesCollectionRef = chatDocument.getReference().collection("messages");
//
//                    // Obtener todas las referencias a los documentos de mensajes
//                    messagesCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot messagesSnapshot) {
//                            for (QueryDocumentSnapshot messageDocument : messagesSnapshot) {
//
//                                messageRefs.add(messageDocument.getReference());
//                                System.out.println("------------------------------------------------"+messageDocument.get("sender"));
//                            }
//
//                            // Verificar si se han obtenido todas las referencias de mensajes
//                            if (messageRefs.size() == querySnapshot.size()) {
//                                // Obtener los documentos correspondientes a las referencias
//                                for (DocumentReference messageRef : messageRefs) {
//                                    messageRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onSuccess(DocumentSnapshot messageSnapshot) {
//
//                                            tuUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                            String content = messageSnapshot.getString("content");
//                                            Date timestamp = messageSnapshot.getDate("timestamp");
//                                            String userId = messageSnapshot.getString("sender");
//                                            String receiver = messageSnapshot.getString("receiver");
//
//                                            // Verificar si tu UID aparece en "user" o "receiver"
//
//                                            if (userId.equals(tuUid) || receiver.equals(tuUid)) {
//                                                myObjects.add(new MyEmails(R.drawable.users1, userId, timestamp.toString(), content));
//                                            }
//
//                                            // Verificar si se han obtenido todos los mensajes
//                                            if (myObjects.size() == messageRefs.size()) {
//                                                // Ordenar la lista de mensajes por timestamp
//                                                Collections.sort(myObjects, new Comparator<MyEmails>() {
//                                                    @Override
//                                                    public int compare(MyEmails email1, MyEmails email2) {
//                                                        return email2.getFecha().compareTo(email1.getFecha());
//                                                    }
//                                                });
//
//                                                // Crear el adaptador y establecerlo en el recyclerView
//                                                MyAdapterEmails adapter = new MyAdapterEmails(myObjects);
//                                                recyclerView.setAdapter(adapter);
//                                                System.out.println("Tamaño del array: " + myObjects.size());
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }
@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    List<MyEmails> myObjects = new ArrayList<>();
    CollectionReference chatsCollectionRef = FirebaseFirestore.getInstance().collection("chats");

    // Obtener todas las subcolecciones de "chats"
    chatsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            myObjects.clear();  // Limpiar la lista antes de agregar nuevos elementos

            for (QueryDocumentSnapshot chatDocument : querySnapshot) {
                // Obtener la referencia de la última subcolección de mensajes
                CollectionReference messagesCollectionRef = chatDocument.getReference().collection("messages");

                // Obtener el último documento de la subcolección de mensajes
                messagesCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot messageDocument : queryDocumentSnapshots) {
                                    String content = messageDocument.getString("content");
                                    Date timestamp = messageDocument.getDate("timestamp");
                                    String userId = messageDocument.getString("sender");
                                    String timestampEmail = dateFormat.format(timestamp);

                                    if(userId.equals(tuUid))fetchUserData(tuUid);
                                    else fetchUserData(userId);


                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    //Temporizador para actualizar la variable antes de mostrarla (Si no se muestra a 0)

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 3000ms
                                            myObjects.add(new MyEmails(photoActualizerd, nameActualized, timestampEmail, content));
                                            // Verificar si se han obtenido todos los mensajes
                                            if (myObjects.size() == querySnapshot.size()) {
                                                MyAdapterEmails adapter = new MyAdapterEmails(myObjects);
                                                System.out.println(photoActualizerd);
                                                recyclerView.setAdapter(adapter);
                                                System.out.println("Tamaño del array: " + myObjects.size());
                                            }
                                        }
                                    }, 200);
                                }
                            }
                        });
                      }
                   }
        });
}

//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        List<MyEmails> myObjects = new ArrayList<>();
//        CollectionReference chatsCollectionRef = FirebaseFirestore.getInstance().collection("chats");
//
//        // Obtener todas las subcolecciones de "chats"
//        chatsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                myObjects.clear();  // Limpiar la lista antes de agregar nuevos elementos
//
//                for (QueryDocumentSnapshot chatDocument : querySnapshot) {
//                    // Obtener la referencia de la última subcolección de mensajes
//                    CollectionReference messagesCollectionRef = chatDocument.getReference().collection("messages");
//
//                    // Obtener el último documento de la subcolección de mensajes
//                    messagesCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
//                            .limit(1)
//                            .get()
//                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    for (QueryDocumentSnapshot messageDocument : queryDocumentSnapshots) {
//                                        String content = messageDocument.getString("content");
//                                        Date timestamp = messageDocument.getDate("timestamp");
//                                        String userId = messageDocument.getString("sender");
//                                        myObjects.add(new MyEmails(R.drawable.users1, userId, timestamp.toString(), content));
//                                    }
//
//                                    // Verificar si se han obtenido todos los mensajes
//
//                                    if (myObjects.size() == querySnapshot.size()) {
//                                        MyAdapterEmails adapter = new MyAdapterEmails(myObjects);
//                                        recyclerView.setAdapter(adapter);
//                                        System.out.println("Tamaño del array: " + myObjects.size());
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//    }
//
private void fetchUserData(String uid) {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = firestore.collection("usuariosPrueba");
    Query query = collectionRef.whereEqualTo("uid", uid);

    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException exception) {
            if (exception != null) {
                // Error al obtener los datos de Firestore
                Log.e("Firestore", "Error al obtener los datos de Firestore", exception);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                // Existe al menos un documento en la colección con el UID proporcionado
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    String documentId = documentSnapshot.getId();
                    String name = documentSnapshot.getString("name");
                    String photoUrl = documentSnapshot.getString("uidPhotoUrl");

                    // Realiza las acciones necesarias con los datos obtenidos

                    nameActualized=name;
                    photoActualizerd=photoUrl;
                    System.out.println( nameActualized+"-------------------------------------------------------------------------name");
                    System.out.println( photoActualizerd+"-------------------------------------------------------------------------photoUrl");

                }
            } else {
                // No existe ningún documento en la colección con el UID proporcionado
                Log.d("Firestore", "No se encontró ningún documento con el UID: " + uid);
            }
        }
    });
}


}
