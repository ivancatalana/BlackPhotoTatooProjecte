package com.example.blackphototatoo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AiChatAdapter extends RecyclerView.Adapter<AiChatAdapter.ChatViewHolder> {
    private List<AiChatMessage> messages;
    private Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public AiChatAdapter(List<AiChatMessage> messages, Context context) {
        this.messages = messages;
        this.context=context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ai_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        AiChatMessage message = messages.get(position);

        holder.promptText.setText(message.getPrompt());

         // Carga la imagen desde una URI
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Se tienen los permisos, cargar la imagen
            Glide.with(holder.itemView)
                    .load(message.getImageUri())
                    .into(holder.imageUriImageView);
        } else {
            // No se tienen los permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        // Cargamos la imagen del server
        Glide.with(holder.itemView)
                .load(message.getImageUrl())
                .into(holder.imageUrlImageView);

        // Muestra la fecha utilizando el formato deseado
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(message.getDate());
        holder.dateTextView.setText(formattedDate);
        holder.promptTextDown.setText(message.getPrompt());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessages(List<AiChatMessage> newMessages) {
        // Invierte el orden de la lista de mensajes
        Collections.reverse(newMessages);
        // Agrega los mensajes invertidos a tu lista de mensajes actual
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView promptText;
        TextView promptTextDown;
        ImageView imageUriImageView;
        ImageView imageUrlImageView;
        TextView dateTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            promptText = itemView.findViewById(R.id.promptTextView);
            promptTextDown = itemView.findViewById(R.id.promptView);
            imageUriImageView = itemView.findViewById(R.id.imageUriImageView);
            imageUrlImageView = itemView.findViewById(R.id.imageUrlImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
    public void addMessagesAtStart(List<AiChatMessage> messages) {
        this.messages.addAll(0, messages);
        notifyDataSetChanged();
    }
}
