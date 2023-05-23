package com.example.blackphototatoo;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DiscoverFirebaseFragment extends Fragment {
    NavController navController;   // <-----------------
    public AppViewPModel appViewModel;
    private Parcelable recyclerViewState;
    RecyclerView publicacionesRecyclerView;
    public DiscoverFirebaseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

// Evita volver atras
//
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                //  Handle the back button even
//                // Aqui podemos configurar el comprotamiento del boton back
//                Log.d("BACKBUTTON", "Back button clicks");
//            }
//        };
//
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
//

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_firebase, container, false);

        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.publicacionesRecyclerView);

        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //myInterface.unlockDrawer();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
//
//        // Restaurar el estado del RecyclerView
//        if (savedInstanceState != null) {
//            recyclerViewState = savedInstanceState.getParcelable("recycler_state");
//            try {
//
//                publicacionesRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//            }
//            catch ( NullPointerException n ){
//                System.out.println("error");
//            }
//        }
        Query query = FirebaseFirestore.getInstance().collection("publications").orderBy("ordenadaDateTime", Query.Direction.DESCENDING).limit(50);

        // Obtiene una referencia al RecyclerView desde su vista
        RecyclerView publicacionesRecyclerView = view.findViewById(R.id.publicacionesRecyclerView);

//        // Crea las opciones del adaptador de FirestoreRecyclerOptions
//        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
//                .setQuery(query, Publicacion.class)
//                .build();
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query, Publicacion.class)
                .setLifecycleOwner(this)
                .build();

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewPModel.class);

        // Crea una instancia del adaptador y pásale las opciones y el contexto
        PublicacionesAdapter adapter = new PublicacionesAdapter(options, requireContext(),appViewModel, navController);

        // Configura el adaptador en el RecyclerView
        publicacionesRecyclerView.setAdapter(adapter);

     //   publicacionesRecyclerView = view.findViewById(R.id.postsRecyclerView);
//
//        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
//                .setQuery(query, Publicacion.class)
//                .setLifecycleOwner(this)
//                .build();

       // publicacionesRecyclerView.setAdapter(new PublicacionesAdapter(options,requireContext()));



    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewState = publicacionesRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recycler_state", recyclerViewState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (publicacionesRecyclerView != null && publicacionesRecyclerView.getLayoutManager() != null) {
            publicacionesRecyclerView.getLayoutManager().removeAllViews(); // Limpia el RecyclerView
        }
    }
}

