package com.example.blackphototatoo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFriendsFragment extends Fragment {
    String photoOK = null;
    private ImageView friendPhotoImageView;
    TextView displayNameTextView, emailTextView;
    NavController navController;
    private Button emailButton;
    MainActivity mainActivity;
    public AppViewModel appViewModel;
    String uidPostProfile;
    TextView contadorPosts;
    int numberOfPosts;
    RecyclerView postsRecyclerView;
    private Parcelable recyclerViewState;
    private  String photoUrl;
    private  String name;
    public ProfileFriendsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Recibimos el uid desde el bundle
        Bundle bundle = getArguments();
        if (bundle != null) uidPostProfile = bundle.getString("uid");

        //////    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
        View view = inflater.inflate(R.layout.fragment_profile_friends, container, false);
        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerProfile);
        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendPhotoImageView = view.findViewById(R.id.friendProfileImageView);
        emailButton = view.findViewById(R.id.messageButton);
        displayNameTextView = view.findViewById(R.id.nameProfile);
        contadorPosts = view.findViewById(R.id.publishedImages);
////--------------------------------------Nos conectamos a la coleccion de usuarios con las imagenes y nombres de usuario actualizados
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionRef = firestore.collection("usuariosPrueba");
        com.google.firebase.firestore.Query query = collectionRef.whereEqualTo("uid", uidPostProfile);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException exception) {
                if (exception != null) {
                    // Error al obtener los datos de Firestore
                    System.out.println("DATABASE error ---------------------------------------__---");
                    return;
                }
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Existe al menos un documento en la colección donde el valor del atributo "mail" es igual al correo del usuario actual
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        String documentId = documentSnapshot.getId();
                        System.out.println(documentSnapshot.get("name") + "----------------------------getter");
                        // Accede a los datos del documento
                        name = documentSnapshot.getString("name");
                        photoUrl = documentSnapshot.getString("uidPhotoUrl");
                        // Realiza las acciones necesarias con los datos obtenidos
                        displayNameTextView.setText(name);
                        Glide.with(requireView()).load(photoUrl).circleCrop().into(friendPhotoImageView);
                    }
                } else {
                    // No existe ningún documento en la colección con el valor del atributo "mail" igual al correo del usuario actual
                    System.out.println("---------------------------------------------------------------------NO--EXISTE");
                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Restaurar el estado del RecyclerView
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_state");
            try {

                postsRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            } catch (NullPointerException n) {
                System.out.println("error");
            }
        }
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle arguments = new Bundle();
                arguments.putString("senderUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                arguments.putString("receiverUid", uidPostProfile);
                arguments.putString("photoURL", photoUrl );
                arguments.putString("name", name );
                 navController.navigate(R.id.emailFragment,arguments);
            }
        });
//
//        postsRecyclerView = view.findViewById(R.id.recyclerProfile);
//        Query query = FirebaseFirestore.getInstance().collection("posts").orderBy("ordenadaDateTime", Query.Direction.DESCENDING).limit(50);
//
//        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
//                .setQuery(query, Post.class)
//                .setLifecycleOwner(this)
//                .build();
//
//        postsRecyclerView.setAdapter(new PostsAdapter(options));
//
//        appViewModel = new
//                ViewModelProvider(requireActivity()).get(AppViewModel.class);
//
//
//        FirebaseFirestore.getInstance()
//                .collection("posts")
//                .whereEqualTo("uid", uidPostProfile)
//                .orderBy("ordenadaDateTime", Query.Direction.DESCENDING).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        System.out.println("-------------------------------querySnapshot.size()     -- " + querySnapshot.size());
//                        numberOfPosts = (int) querySnapshot.size();
//
//                    }
//                });

        postsRecyclerView = view.findViewById(R.id.recyclerProfile);

        // Consulta con indice compuesto que obliga a estar  habilitada en firebase
        Query query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("uid", uidPostProfile).orderBy("ordenadaDateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();
        PostsAdapter adapter = new ProfileFriendsFragment.PostsAdapter(options);
        postsRecyclerView.setAdapter(adapter);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        final Handler handler = new Handler(Looper.getMainLooper());

        //Temporizador para actualizar la variable antes de mostrarla (Si no se muestra a 0)

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                contadorPosts.setText("" + numberOfPosts);
            }
        }, 200);

    }


    class PostsAdapter extends FirestoreRecyclerAdapter<Post, ProfileFriendsFragment.PostsAdapter.PostViewHolder> {
        // Metodo para contar los posts
        @Override
        public int getItemCount() {
            return super.getItemCount(); // Retorna el número de items del adaptador
        }

        public PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
            super(options);
        }

        @NonNull
        @Override
        public ProfileFriendsFragment.PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProfileFriendsFragment.PostsAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.objeto_imagen_layout, parent, false));
        }


        @Override

        protected void onBindViewHolder(@NonNull ProfileFriendsFragment.PostsAdapter.PostViewHolder holder, int position, @NonNull final Post post) {

            Glide.with(getContext()).load(post.mediaUrl).circleCrop().into(holder.mediaImageView);
            holder.dateTimeTextView.setText(post.dateTimePost);
            holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
            holder.namePostsProfile.setText(post.content);
        }
        class PostViewHolder extends RecyclerView.ViewHolder {
            ImageView  mediaImageView;
            TextView dateTimeTextView, numLikesTextView , namePostsProfile;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);
                //   postPhotoImageView=itemView.findViewById(R.id.photoImageView);
                mediaImageView = itemView.findViewById(R.id.postsProfileImageView);
                dateTimeTextView = itemView.findViewById(R.id.postsProfileDate);
                numLikesTextView = itemView.findViewById(R.id.rankingfoto);
                namePostsProfile = itemView.findViewById(R.id.postsProfileName);

            }
        }
    }

    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewState = postsRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recycler_state", recyclerViewState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postsRecyclerView != null && postsRecyclerView.getLayoutManager() != null) {
            postsRecyclerView.getLayoutManager().removeAllViews(); // Limpia el RecyclerView
        }
    }
}
