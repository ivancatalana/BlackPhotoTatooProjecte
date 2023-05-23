package com.example.blackphototatoo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PublicacionesViewHolder extends RecyclerView.ViewHolder {
    public ImageView authorPhotoImageView;
    public TextView authorTextView;
    public TextView dateTimeTextView;
    public TextView contentTextView;
    public ImageView deleteImageView;
    public ImageView likeImageView;
    public TextView numLikesTextView;
    public ImageView mediaImageView;

    public PublicacionesViewHolder(View itemView) {
        super(itemView);
        // Inicializa los elementos de la vista del ViewHolder aquí
        authorPhotoImageView = itemView.findViewById(R.id.photo_image_view);
        authorTextView = itemView.findViewById(R.id.authorTextView);
        dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
        contentTextView = itemView.findViewById(R.id.contentTextView);
        deleteImageView = itemView.findViewById(R.id.deleteImageView);
        likeImageView = itemView.findViewById(R.id.likeImageView);
        numLikesTextView = itemView.findViewById(R.id.numLikesTextView);
        mediaImageView = itemView.findViewById(R.id.mediaImage);
    }
}




