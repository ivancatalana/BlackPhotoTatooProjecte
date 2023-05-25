package com.example.blackphototatoo;

import android.os.Bundle;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.bottom1Fragment, new EmailFragment())
                        .addToBackStack(null)
                        .commit();


            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//        String timestamp = dateFormat.format(message.getTimestamp());
        List<MyEmails> myObjects = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chats")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String chatId = document.getId();
                            // Utiliza el chatId como desees, por ejemplo, imprimirlo
                            System.out.println("ID del chat: " + chatId);
                        }
                    }
                });
//
//        chatsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                myObjects.clear();  // Limpiar la lista antes de agregar nuevos elementos
//
//                for (QueryDocumentSnapshot chatDocument : querySnapshot) {
//                    String chatId = chatDocument.getId();
//                    CollectionReference messagesCollectionRef = chatDocument.getReference().collection("messages");
//
//                    messagesCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
//                            .limit(1)
//                            .get()
//                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    for (QueryDocumentSnapshot messageDocument : queryDocumentSnapshots) {
//                                        String content = messageDocument.getString("content");
//                                        Date timestamp = messageDocument.getDate("timestamp");
//                                        String userId = messageDocument.getString("user");
//                                        myObjects.add(new MyEmails(R.drawable.users1, userId, timestamp.toString(), content));
//                                    }
//
//                                    // Verificar si se han obtenido todos los mensajes
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
    }



}
