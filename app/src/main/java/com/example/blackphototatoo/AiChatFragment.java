package com.example.blackphototatoo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AiChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private AiChatAdapter adapter;
    private Button loadMoreButton;
    private boolean isInitialLoad = true; // Nuevo flag para controlar la carga inicial de mensajes
    private List<AiChatMessage> messages;
    private AiChatAdapter tuAdaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        loadMoreButton = view.findViewById(R.id.loadMoreButton);

        messages = new ArrayList<>();
        adapter = new AiChatAdapter(messages, requireContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true); // Cambio aquí: establece el diseño en reversa
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInitialLoad) {
                    loadMoreMessages();
                }
            }
        });

        if (isInitialLoad) {
            getInitialMessages();
            isInitialLoad = false;
        }

        return view;
    }
    private void getInitialMessages() {
        String uidUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Obtén una referencia a la subcolección "publicaciones" del usuario actual
        CollectionReference publicacionesRef = firestore.collection("serverEditions").document(uidUsuarioActual).collection("publicaciones");

        // Crea una consulta para obtener los mensajes ordenados por fecha en orden descendente y limita a 3 mensajes
        Query query = publicacionesRef.orderBy("time", Query.Direction.DESCENDING).limit(2);

        // Ejecuta la consulta
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<AiChatMessage> initialMessages = new ArrayList<>();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String prompt = documentSnapshot.getString("prompt");
                    String rutaImagen = documentSnapshot.getString("rutaImagen");
                    String rutaImagenRespuesta = documentSnapshot.getString("rutaImagenRespuesta");
                    Date fecha = documentSnapshot.getDate("time");
                    // Obtén el URI de la imagen utilizando Glide u otra biblioteca de tu elección
                    // Obtén el Uri de la imagen a partir de la ruta de la imagen
                    Uri imageUri = Uri.parse(rutaImagen);
                    // Crea un objeto AiChatMessage y agrégalo a la lista
                    AiChatMessage chatMessage = new AiChatMessage(prompt, imageUri, rutaImagenRespuesta, fecha);
                    initialMessages.add(chatMessage);
                }
// Invierte el orden de los mensajes iniciales
                Collections.reverse(initialMessages);

// Agrega los mensajes a tu adaptador y notifica los cambios
                adapter.addMessages(initialMessages);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Maneja el error de consulta según tus necesidades
            }
        });
    }
    private void loadMoreMessages() {
        String uidUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        CollectionReference publicacionesRef = firestore.collection("serverEditions").document(uidUsuarioActual).collection("publicaciones");

        AiChatMessage lastMessage = messages.get(messages.size() - 1); // Cambio aquí: obtener el último mensaje

        Query query = publicacionesRef.orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastMessage.getDate()) // Cambio aquí: usar startAfter en lugar de endBefore
                .limit(3);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<AiChatMessage> moreMessages = new ArrayList<>();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String prompt = documentSnapshot.getString("prompt");
                    String rutaImagen = documentSnapshot.getString("rutaImagen");
                    String rutaImagenRespuesta = documentSnapshot.getString("rutaImagenRespuesta");
                    Date fecha = documentSnapshot.getDate("time");
                    Uri imageUri = Uri.parse(rutaImagen);
                    AiChatMessage chatMessage = new AiChatMessage(prompt, imageUri, rutaImagenRespuesta, fecha);
                    moreMessages.add(chatMessage);
                }

                /// Invierte el orden de los mensajes iniciales
                Collections.reverse(moreMessages);

                // Agrega los mensajes a tu adaptador y notifica los cambios
                adapter.addMessages(moreMessages);

                if (moreMessages.size() < 3) {
                    loadMoreButton.setEnabled(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Maneja el error de consulta según tus necesidades
            }
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Verifica si la actividad implementa la interfaz FragmentReloadListener
        if (context instanceof AiChatAdapter.FragmentReloadListener) {
            // Crea el adaptador y pasa la instancia de la actividad como listener
            tuAdaptador = new AiChatAdapter((AiChatAdapter.FragmentReloadListener) context);
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar FragmentReloadListener");
        }
    }
}
