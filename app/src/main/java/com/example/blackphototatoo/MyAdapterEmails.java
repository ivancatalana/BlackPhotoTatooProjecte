package com.example.blackphototatoo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapterEmails extends RecyclerView.Adapter<MyAdapterEmails.MyViewHolder> {
    private List<MyEmails> myEmails;
    private OnItemClickListener itemClickListener;


    public MyAdapterEmails(List<MyEmails> myEmails) {
        this.myEmails = myEmails;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_email_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyEmails myObject = myEmails.get(position);
        // Cargar la imagen usando Glide en el ImageView del holder
        Glide.with(holder.itemView.getContext())
                .load(myObject.getImageResource())
                .circleCrop()
                .into(holder.imageView);

        // Establecer los demás datos en los elementos del holder
        holder.name.setText(myObject.getName());
        holder.fecha.setText(myObject.getFecha());
        holder.textView.setText(myObject.getText());

        // Configurar el click listener en el ViewHolder
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MyEmails email = myEmails.get(position);
                        itemClickListener.onItemClick(email);
                    }
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(MyEmails email);
    }


    @Override
    public int getItemCount() {
        return myEmails.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView fecha;
        public TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.postsProfileImageView);
            textView = itemView.findViewById(R.id.rankingfoto);
            fecha = itemView.findViewById(R.id.postsProfileDate);
            name = itemView.findViewById(R.id.postsProfileName);


        }
    }
}

