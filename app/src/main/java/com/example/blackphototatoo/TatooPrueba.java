package com.example.blackphototatoo;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class TatooPrueba extends Fragment {
    private RecyclerView recyclerView;
    public AppViewModel appViewModel;
    RecyclerView postsRecyclerView;
    private Parcelable recyclerViewState;
    NavController navController;
    String uidPostProfile;
    private String photoUrl;
    private String name;

    public TatooPrueba() {
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
        View view = inflater.inflate(R.layout.fragment_tatoo_prueba, container, false);
        //////    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
        // Find the RecyclerView in the layout
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewchatList);
//        // Set the layout manager
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postsRecyclerView = view.findViewById(R.id.recyclerTiendas);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Adjuntar un LinearLayoutManager al RecyclerView

        uidPostProfile = FirebaseAuth.getInstance().getUid();

//        Query query = FirebaseFirestore.getInstance().collection("usuariosPrueba").whereEqualTo("premium", true);
//
//        FirestoreRecyclerOptions<PremiumUser> options = new FirestoreRecyclerOptions.Builder<PremiumUser>()
//                .setQuery(query, PremiumUser.class)
//                .setLifecycleOwner(this)
//                .build();
//
//        PremiumUserAdapter adapter = new PremiumUserAdapter(options);
//        postsRecyclerView.setAdapter(adapter);
//
//        // Obtener el recuento de usuarios premium
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    int count = 0;
//                    QuerySnapshot querySnapshot = task.getResult();
//                    if (querySnapshot != null) {
//                        count = querySnapshot.size();
//                    }
//
//                    System.out.println("Usuarios Premium --------------------------------------------------------------------------------" + count);
//                    // Aquí puedes usar el valor del contador (count) como desees
//                    // por ejemplo, mostrarlo en un TextView o realizar alguna otra acción.
//                    // ...
//                } else {
//                    // Manejar errores en la consulta
//                    Exception exception = task.getException();
//                    // ...
//                }
//            }
//        });
        List<PremiumUser> premiumUsers = new ArrayList<>();

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionRef = firestore.collection("usuariosPrueba");
        Query query = collectionRef.whereEqualTo("premium", true);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException exception) {
                if (exception != null) {
                    // Error al obtener los datos de Firestore
                    return;
                }

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Existen documentos que cumplen el filtro

                    // Recorrer los documentos obtenidos
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        // Obtener los datos del documento
                        String name = documentSnapshot.getString("name");
                        String photoUrl = documentSnapshot.getString("uidPhotoUrl");
                        String email = documentSnapshot.getString("mail");
                        String uid = documentSnapshot.getString("uid");
//
//                        // Crear un objeto PremiumUser y agregarlo a la lista
//                        PremiumUser premiumUser = new PremiumUser(name, photoUrl, email ,uid);
//                        premiumUsers.add(premiumUser);
//

                    }
                    // Crear el adapter y asignarlo al RecyclerView
                    PremiumUserAdapter adapter = new PremiumUserAdapter(premiumUsers);
                    postsRecyclerView.setAdapter(adapter);
                } else {
                    // No existen documentos que cumplan el filtro
                }
            }
        });
    }

}