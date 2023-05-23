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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFriendsFragment extends Fragment {
    String photoOK = null;
    ImageView postPhotoImageView;
    TextView displayNameTextView, emailTextView;
    NavController navController;
    MainActivity mainActivity;
    public AppViewModel appViewModel;
    String uidPostProfile;
    TextView contadorPosts;
    int numberOfPosts;
    RecyclerView postsRecyclerView;
    private Parcelable recyclerViewState;

    public ProfileFriendsFragment() {
    }

    public static ProfileFriendsFragment newInstance() {
        return new ProfileFriendsFragment();
    }

    public ProfileFriendsFragment setPostProfile(int photoId, String displayName, String email) {
        Bundle bundle = new Bundle();
        bundle.putInt("photoId", photoId);
        bundle.putString("displayName", displayName);
        bundle.putString("email", email);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //////    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla

        View view = inflater.inflate(R.layout.fragment_profile_friends, container, false);
        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerProfile);
        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postPhotoImageView = view.findViewById(R.id.friendProfileImageView);
        displayNameTextView = view.findViewById(R.id.nameProfile);
        contadorPosts = view.findViewById(R.id.publishedImages);
        //////  // Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
//
//        Bundle args = getArguments();
//        if (args != null) {
//            postPhotoImageView = view.findViewById(R.id.friendProfileImageView);
//            displayNameTextView = view.findViewById(R.id.nameProfile);
//            contadorPosts = view.findViewById(R.id.publisheImages);
//            //emailTextView = view.findViewById(R.id.emailTextView);
//            displayNameTextView.setText(args.getString("nombre"));
////            emailTextView.setText(args.getString("email"));
//            uidPostProfile = args.getString("email");
//            //
//            //     postPhotoImageView.setImageResource(args.getInt("foto"));
//
//        }
//
//        // ...

        // Inicializar el objeto AppViewModel
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.postSeleccionado.observe(getViewLifecycleOwner(), post ->
        {
           uidPostProfile=post.uid;
          // displayNameTextView.setText(post.author);
           //Glide.with(requireView()).load(post.authorPhotoUrl).circleCrop().into(postPhotoImageView);

        });
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("users").document(uidPostProfile);
//


        // ------------------------------------- Intento de recibir la ultima foto de los usuarios
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("uid", uidPostProfile);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Accede a los datos del documento
                        String name = document.getString("name");
                        String photoUrl = document.getString("uidPhotoUrl");
                        // Realiza las acciones necesarias con los datos obtenidos
                        displayNameTextView.setText(name);
                        Glide.with(requireView()).load(photoUrl).circleCrop().into(postPhotoImageView);
                        System.out.println(name+"--------------------------------------------------Name");
                        System.out.println(photoUrl+"-----------------------------------------------------------------Photo");


                    }
                } else {
                    // Ocurrió un error al obtener los datos
                    Exception e = task.getException();
                    // Maneja el error adecuadamente
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
            }
            catch ( NullPointerException n ){
                System.out.println("error");
            }
        }

        FirebaseFirestore.getInstance()
                .collection("posts")
                .whereEqualTo("uid", uidPostProfile)
                .orderBy("ordenadaDateTime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        System.out.println("-------------------------------querySnapshot.size()     -- " + querySnapshot.size());
                        numberOfPosts = (int) querySnapshot.size();

                    }
                });
        postsRecyclerView = view.findViewById(R.id.recyclerProfile);
        // System.out.println(uidPostProfile + " EL uid del post ------------------------------------------------------------");
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
            return new ProfileFriendsFragment.PostsAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_post, parent, false));
        }


        @Override

        protected void onBindViewHolder(@NonNull ProfileFriendsFragment.PostsAdapter.PostViewHolder holder, int position, @NonNull final Post post) {


            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //FirebaseAuth.getInstance();
            System.out.println("------------" + post.authorPhotoUrl);

            if (post.authorPhotoUrl.contains("http") || post.authorPhotoUrl.contains("content")) {
                photoOK = post.authorPhotoUrl;
            }
            if (photoOK == null)
                Glide.with(requireView()).load(getResources().getDrawable(R.drawable.profile)).circleCrop().into(holder.authorPhotoImageView);
            else
                Glide.with(getContext()).load(photoOK).circleCrop().into(postPhotoImageView);


            if (post.authorPhotoUrl != null) {
                Glide.with(getContext()).load(post.authorPhotoUrl).circleCrop().into(holder.authorPhotoImageView);
            }
            if (post.author!=null) holder.authorTextView.setText(post.author);
            else{
                holder.authorTextView.setText("");
            }
            holder.dateTimeTextView.setText(post.dateTimePost);

            holder.contentTextView.setText(post.content);

            //   holder.contentTextView.setText(post.dateTimePost);
            // Gestion de likes
            final String postKey = getSnapshots().getSnapshot(position).getId();
            if (post.likes.containsKey(uid))
                holder.likeImageView.setImageResource(R.drawable.like_on);
            else holder.likeImageView.setImageResource(R.drawable.like_off);
            holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
            holder.likeImageView.setOnClickListener(view -> {
                FirebaseFirestore.getInstance().collection("posts")
                        .document(postKey)
                        .update("likes." + uid, post.likes.containsKey(uid) ? FieldValue.delete() : true);
            });

            //Gestion de borrados
            holder.deleteImageView.setVisibility(View.GONE);


            // Miniatura de media
            if (post.mediaUrl != null) {
                holder.mediaImageView.setVisibility(View.VISIBLE);
                if ("audio".equals(post.mediaType)) {
                    Glide.with(requireView()).load(R.drawable.audio).centerCrop().into(holder.mediaImageView);
                } else {
                    Glide.with(requireView()).load(post.mediaUrl).centerCrop().into(holder.mediaImageView);
                }
                holder.mediaImageView.setOnClickListener(view -> {
                    appViewModel.postSeleccionado.setValue(post);
                    navController.navigate(R.id.mediaFragment);
                });
            } else {
                holder.mediaImageView.setVisibility(View.GONE);
            }
        }


        class PostViewHolder extends RecyclerView.ViewHolder {
            ImageView authorPhotoImageView, likeImageView, deleteImageView, mediaImageView;
            TextView authorTextView, dateTimeTextView, contentTextView, numLikesTextView;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);
                //   postPhotoImageView=itemView.findViewById(R.id.photoImageView);
                authorPhotoImageView = itemView.findViewById(R.id.photoImageView);
                likeImageView = itemView.findViewById(R.id.likeImageView);
                mediaImageView = itemView.findViewById(R.id.mediaImage);
                dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
                authorTextView = itemView.findViewById(R.id.authorTextView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                numLikesTextView = itemView.findViewById(R.id.numLikesTextView);
                deleteImageView = itemView.findViewById(R.id.deleteImageView);

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
