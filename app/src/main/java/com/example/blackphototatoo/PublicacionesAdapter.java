package com.example.blackphototatoo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
public class PublicacionesAdapter extends FirestoreRecyclerAdapter<Publicacion, PublicacionesViewHolder> {
    private AppViewPModel appViewModel;
    private NavController navController;   // <-----------------
    private Context context;

    public PublicacionesAdapter(@NonNull FirestoreRecyclerOptions<Publicacion> options, Context context,AppViewPModel p, NavController n) {
        super(options);
        this.context = context;
        this.appViewModel = p;
        this.navController = n;
    }

    @Override
    protected void onBindViewHolder(@NonNull PublicacionesViewHolder holder, int position, @NonNull final Publicacion post) {
        // Implementa la lógica de binding del view holder aquí
        // ...
        if (post.authorPhotoUrl != null) {
         //   Glide.with(context).load(post.authorPhotoUrl).circleCrop().into(holder.authorPhotoImageView);
        } else {
            // Manejo del caso en el que post.authorPhotoUrl es nulo
            // Por ejemplo, puedes cargar una imagen de marcador de posición o dejar el ImageView vacío
        }

        holder.authorTextView.setText(post.author);
        holder.dateTimeTextView.setText(post.dateTimePost);

        holder.contentTextView.setText(post.nombre);
        holder.deleteImageView.setVisibility(View.GONE);

        //   holder.contentTextView.setText(post.dateTimePost);
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
//        holder.authorPhotoImageView.setOnClickListener(view -> {
//            appViewModel.postSeleccionado.setValue(post);
//            //
//            //
//            Bundle bundle = new Bundle();
//
//            bundle.putString("nombre", post.author);
//            bundle.putString("email", post.uid);
//            bundle.putInt("foto", R.drawable.profile);

/*
                if (post.uid.equals(uid)){
                    navController.navigate(R.id.nameProfile);
               }

 */
                /*
                else{
                    navController.navigate(R.id.postProfileFragment,bundle);

                }

           */




       // });
        holder.contentTextView.setOnClickListener(view -> {
            appViewModel.postSeleccionado.setValue(post);
            //
            //
            //PostProfileFragment postProfileFragment = PostProfileFragment.newInstance().setPostProfile(R.drawable.profile,post.author,post.uid);
            Bundle bundle = new Bundle();
            System.out.println(post.nombre+"  ");

            bundle.putString("nombre", post.author);
            bundle.putString("time",post.dateTimePost);
            bundle.putString("contenido", post.nombre);
            bundle.putString("foto", post.authorPhotoUrl);
            if (post.mediaUrl != null)    bundle.putString("fotoMedia", post.mediaUrl);


            //Navegamos al postView para ver los comentarios
            navController.navigate(R.id.postViewFragment,bundle);



        });


        // Miniatura de media
        if (post.mediaUrl != null) {
            holder.mediaImageView.setVisibility(View.VISIBLE);

            Glide.with(context).load(post.mediaUrl).centerCrop().into(holder.mediaImageView);

            holder.mediaImageView.setOnClickListener(view -> { appViewModel.postSeleccionado.setValue(post); navController.navigate(R.id.mediaFragment);
            });
        } else { holder.mediaImageView.setVisibility(View.GONE);
        }

    }

    @NonNull
    @Override
    public PublicacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_publicacion, parent, false);
        return new PublicacionesViewHolder(view);
    }
}
