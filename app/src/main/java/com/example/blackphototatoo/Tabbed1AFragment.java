package com.example.blackphototatoo;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Tabbed1AFragment extends Fragment {
    private RecyclerView recyclerView;
    public AppViewModel appViewModel;
    RecyclerView postsRecyclerView;
    private Parcelable recyclerViewState;
    NavController navController;
    String uidPostProfile;
    private  String photoUrl;
    private  String name;


    public Tabbed1AFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_tabbed1_a, container, false);
        //////    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerProfile);
        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        postsRecyclerView = view.findViewById(R.id.recyclerProfile);
        uidPostProfile= FirebaseAuth.getInstance().getUid();        // Consulta con indice compuesto que obliga a estar  habilitada en firebase
        Query query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("uid", uidPostProfile).orderBy("ordenadaDateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();
        PostsAdapter adapter = new PostsAdapter(options);
        postsRecyclerView.setAdapter(adapter);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.PostViewHolder> {
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
    public PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.objeto_imagen_layout, parent, false));
    }


    @Override

    protected void onBindViewHolder(@NonNull PostsAdapter.PostViewHolder holder, int position, @NonNull final Post post) {

        Glide.with(getContext()).load(post.mediaUrl).circleCrop().into(holder.mediaImageView);
        holder.dateTimeTextView.setText(post.dateTimePost);
        holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
        holder.namePostsProfile.setText(post.content);
    }
    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaImageView;
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