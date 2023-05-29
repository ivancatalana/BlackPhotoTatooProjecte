package com.example.blackphototatoo;



import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackphototatoo.Post;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFirebaseFragment extends Fragment  implements SearchPostAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SearchPostAdapter adapter;
    private SearchViewModel searchViewModel;
    private FirebaseFirestore firestore;
    public AppViewModel appViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_firebase, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SearchPostAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Inicializar Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

// Realizar la búsqueda al hacer clic en el botón de búsqueda o al escribir en el EditText
        ImageButton searchButton = view.findViewById(R.id.btnSend2);
        EditText searchEditText = view.findViewById(R.id.writeEmail3);
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchEditText.getText().toString();
            performSearch(searchTerm);

            // Borrar el texto del EditText
            searchEditText.setText("");

            // Cerrar el teclado
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        });
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchTerm = searchEditText.getText().toString();
                performSearch(searchTerm);
                System.out.println(searchTerm+"--------------------------------------------------------");
                // Borrar el texto del EditText
                searchEditText.setText("");

                // Cerrar el teclado
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                return true;
            }
            return false;
        });


        return view;
    }

    private void performSearch(String searchTerm) {
        // Realizar la consulta a Firebase Firestore para obtener los resultados de búsqueda
        CollectionReference postsRef = firestore.collection("posts");
        Query query = postsRef.whereEqualTo("content", searchTerm);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Post> searchResults = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Obtener los datos del documento y crear un objeto Post
                Post post = document.toObject(Post.class);
                searchResults.add(post);
            }
            // Asignar la lista de resultados de búsqueda directamente al adaptador
            adapter.getPosts().clear();
            adapter.getPosts().addAll(searchResults);
            adapter.notifyDataSetChanged();
            System.out.println(".................................... size search results : " + searchResults.size());

        }).addOnFailureListener(e -> {
            // Manejar el error en caso de fallo en la consulta
            System.out.println("Error performing search");
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar el listener de clic en el adaptador
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(Post post) {
        searchViewModel.setSelectedPost(post);
        appViewModel.postSeleccionado.setValue(post);
        NavHostFragment.findNavController(this).navigate(R.id.action_searchFragment_to_mediaFragment);

    }

}
