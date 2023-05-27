package com.example.blackphototatoo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Tabbed1CFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<MyEmails> listaEmails =new ArrayList<>();
    private String tuUid;
    private String nameActualized;
    private String photoActualizerd;
    private NavController navController;
    public Tabbed1CFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tabbed1_c, container, false);
        recyclerView = view.findViewById(R.id.recyclerView3);
        tuUid = FirebaseAuth.getInstance().getUid();
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
                System.out.println(tuUid + "-------------------------------------------------" + tuUid);
                for (QueryDocumentSnapshot chatDocument : querySnapshot) {
                    String senderChat = chatDocument.getString("sender");
                    String receiverChat = chatDocument.getString("receiver");

                    System.out.println("Senderxat   : " + senderChat + "  Receiverxat  : " + receiverChat + "-----------------------------------------------");

                    // Verificar si el UID del remitente o receptor coincide con el UID del usuario actual
                    if (senderChat.equals(tuUid) || receiverChat.equals(tuUid)) {
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
                                            String sender = messageDocument.getString("sender");  //uid del usuario1
                                            String receiver = messageDocument.getString("receiver");//uid del usuario 2
                                            String content = messageDocument.getString("content");
                                            Date timestamp = messageDocument.getDate("timestamp");
                                            String userId = messageDocument.getString("sender");
                                            System.out.println("Sender:   " + sender + "  Reveiver  :   " + receiver);
                                            System.out.println(content);
                                            String timestampEmail = dateFormat.format(timestamp);

                                            // Verificar si el UID del remitente o receptor coincide con el UID del usuario actual
                                            fetchUserData(userId, new ChatList.FetchUserDataCallback() {
                                                @Override
                                                public void onUserDataFetched(String name, String photoUrl) {
                                                    MyEmails email = new MyEmails(photoUrl, name, timestampEmail, content , receiver, sender);
                                                    myObjects.add(email); // Agregar objeto a la lista
                                                    navController = Navigation.findNavController(view);

                                                    MyAdapterEmails adapter = new MyAdapterEmails(myObjects);
                                                    adapter.setOnItemClickListener(new MyAdapterEmails.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(MyEmails email) {
                                                            handleEmailClick(view, email);
                                                        }
                                                    });
                                                    recyclerView.setAdapter(adapter);
                                                    System.out.println("Tamaño del array: " + myObjects.size());
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void handleEmailClick(View view,MyEmails email) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String senderUid = email.getSender();
        String receiverUid = email.getReceiver();

        System.out.println(senderUid+"--------------SenderUID---------------------------------------------  "+ senderUid);
        System.out.println(tuUid+"-------------------mi UID----------------------------------------  "+ tuUid);
        System.out.println(receiverUid+"------------------------------------Receiver-----------------------  "+ receiverUid);

        String photoUrl = email.getImageResource();
        String name = email.getName();

        // Configurar los argumentos para pasar al fragmento siguiente
        Bundle arguments = new Bundle();
//        if (currentUid.equals(senderUid)) {
//            // Tú eres el remitente
//            arguments.putString("senderUid", senderUid);
//            arguments.putString("receiverUid", receiverUid);
//        } else {
        // Tú eres el receptor
        arguments.putString("senderUid", receiverUid);
        arguments.putString("receiverUid", senderUid);
        // }
        arguments.putString("photoUrl", photoUrl);
        arguments.putString("name", name);
        navController = Navigation.findNavController(view);

        // Navegar al fragmento siguiente (emailFragment) con los argumentos
        navController.navigate(R.id.emailFragment, arguments);
    }


    private void fetchUserData(String uid, ChatList.FetchUserDataCallback callback) {
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
                        callback.onUserDataFetched(name, photoUrl);
                        System.out.println(name + "-------------------------------------------------------------------------name");
                        System.out.println(photoUrl + "-------------------------------------------------------------------------photoUrl");
                    }
                } else {
                    // No existe ningún documento en la colección con el UID proporcionado
                    Log.d("Firestore", "No se encontró ningún documento con el UID: " + uid);
                }
            }
        });
    }

//    private interface FetchUserDataCallback {
//        void onUserDataFetched(String name, String photoUrl);
//    }

}
