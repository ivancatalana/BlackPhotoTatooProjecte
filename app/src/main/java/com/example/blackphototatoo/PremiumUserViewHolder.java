package com.example.blackphototatoo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
public class PremiumUserViewHolder extends RecyclerView.ViewHolder {
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private RelativeLayout relativeButtonMap;
    private PremiumUser premiumUser;

    public PremiumUserViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImageView = itemView.findViewById(R.id.tatooImageViewStore);
        nameTextView = itemView.findViewById(R.id.tatooNameTextView);
        emailTextView = itemView.findViewById(R.id.tatooEmailTextView);
        relativeButtonMap = itemView.findViewById(R.id.relativeButtonMap);
    }

    public void bind(Context context, PremiumUser premiumUser) {
        // Asignar los valores del usuario premium a las vistas correspondientes
        // Puedes usar una biblioteca de carga de imágenes, como Picasso o Glide, para cargar la imagen desde la URL en el ImageView
        Glide.with(context).load(premiumUser.getProfileImageUrl()).into(profileImageView);
        nameTextView.setText(premiumUser.getName());
        emailTextView.setText(premiumUser.getEmail());
    }

    public RelativeLayout getRelativeButtonMap() {
        return relativeButtonMap;
    }

    public PremiumUser getPremiumUser() {
        return premiumUser;
    }
}
