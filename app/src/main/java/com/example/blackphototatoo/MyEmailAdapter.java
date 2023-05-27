package com.example.blackphototatoo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyEmailAdapter extends RecyclerView.Adapter<MyEmailAdapter.EmailViewHolder> {
    private List<MyEmails> emailList;
    private OnItemClickListener itemClickListener;

    public MyEmailAdapter(List<MyEmails> emailList) {
        this.emailList = emailList;
    }

    public interface OnItemClickListener {
        void onItemClick(MyEmails email);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_email_layout, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        MyEmails email = emailList.get(position);
        holder.bind(email);
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public class EmailViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView timestampTextView;
        private ImageView photoImageView;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.postsProfileName);
            timestampTextView = itemView.findViewById(R.id.postsProfileDate);
            photoImageView = itemView.findViewById(R.id.postsProfileImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                        MyEmails email = emailList.get(position);
                        itemClickListener.onItemClick(email);
                    }
                }
            });
        }

        public void bind(MyEmails email) {
            nameTextView.setText(email.getName());
            timestampTextView.setText(email.getFecha());
            // Cargar la imagen del email usando una biblioteca de imágenes (como Glide o Picasso)
            // photoImageView.loadImage(email.getImageResource());
        }
    }
}
