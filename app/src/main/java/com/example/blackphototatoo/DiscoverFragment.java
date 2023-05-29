package com.example.blackphototatoo;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class DiscoverFragment extends Fragment {
    NavController navController;   // <-----------------
    public AppViewModel appViewModel;
    private Parcelable recyclerViewState;
    RecyclerView postsRecyclerView;
    public DiscoverFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
// Evita volver atras
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //  Handle the back button even
                // Aqui podemos configurar el comprotamiento del boton back
                Log.d("BACKBUTTON", "Back button clicks");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.publishRecyclerView);

        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //myInterface.unlockDrawer();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        Button searchButton = view.findViewById(R.id.searchButtonDiscover);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.searchFirebaseFragment);

            }
        });


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

        postsRecyclerView = view.findViewById(R.id.publishRecyclerView);
        Query query = FirebaseFirestore.getInstance().collection("posts").orderBy("ordenadaDateTime", Query.Direction.DESCENDING).limit(50);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();

        postsRecyclerView.setAdapter(new PostsAdapter(options));
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);


    }


    class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.PostViewHolder> {
        public PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
            super(options);
        }

        @NonNull
        @Override
        public PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PostsAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_object_layout, parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull PostsAdapter.PostViewHolder holder, int position, @NonNull final Post post) {

            Glide.with(getContext()).load(post.authorPhotoUrl).circleCrop().into(holder.authorPhotoImageView);
            holder.authorTextView.setText(post.author);
            holder.dateTimeTextView.setText(post.dateTimePost);
            holder.contentTextView.setText(post.content);

            // Gestion de likes
            final String postKey = getSnapshots().getSnapshot(position).getId();
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(post.likes.containsKey(uid)) holder.likeImageView.setImageResource(R.drawable.like_on);
            else holder.likeImageView.setImageResource(R.drawable.like_off);
            holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
            holder.likeImageView.setOnClickListener(view -> { FirebaseFirestore.getInstance().collection("posts")
                    .document(postKey)
                    .update("likes."+uid, post.likes.containsKey(uid) ? FieldValue.delete() : true);
            });

            holder.authorPhotoImageView.setOnClickListener(view -> {
                appViewModel.postSeleccionado.setValue(post);
                Bundle bundle = new Bundle();
                bundle.putString("nombre", post.author);
                bundle.putString("uid", post.uid);

                if (post.uid.equals(uid)){
                    BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view);
                    bottomNavigationView.setSelectedItemId(R.id.bottom1Fragment);               }

                else{
                    navController.navigate(R.id.profileFriendsFragment,bundle);
                }
            });
            holder.contentTextView.setOnClickListener(view -> {
                appViewModel.postSeleccionado.setValue(post);

                Bundle bundle = new Bundle();
                System.out.println(post.content+"  ");

                bundle.putString("nombre", post.author);
                bundle.putString("time",post.dateTimePost);
                bundle.putString("contenido", post.content);
                bundle.putString("foto", post.authorPhotoUrl);
                if (post.mediaUrl != null) bundle.putString("fotoMedia", post.mediaUrl);


                //Navegamos al postView para ver los comentarios
                navController.navigate(R.id.postViewFragment,bundle);
            });

            // Miniatura de media
            if (post.mediaUrl != null) {
                holder.mediaImageView.setVisibility(View.VISIBLE);

                Glide.with(requireView()).load(post.mediaUrl).centerCrop().into(holder.mediaImageView);

                holder.mediaImageView.setOnClickListener(view -> { appViewModel.postSeleccionado.setValue(post); navController.navigate(R.id.mediaFragment);
                });
            } else { holder.mediaImageView.setVisibility(View.GONE);
            }
        }

        class PostViewHolder extends RecyclerView.ViewHolder {
            ImageView authorPhotoImageView, likeImageView,mediaImageView;
            TextView authorTextView, dateTimeTextView, contentTextView, numLikesTextView;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);
                authorPhotoImageView = itemView.findViewById(R.id.photoImageView);
                likeImageView = itemView.findViewById(R.id.likeImageView);
                mediaImageView = itemView.findViewById(R.id.mediaImage);
                dateTimeTextView =  itemView.findViewById(R.id.dateTimeTextView);
                authorTextView = itemView.findViewById(R.id.authorTextView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                numLikesTextView = itemView.findViewById(R.id.numLikesTextView);
            }
        }
    }
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
