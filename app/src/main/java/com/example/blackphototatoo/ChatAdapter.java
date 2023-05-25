package com.example.blackphototatoo;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<AiChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatList;

    public ChatAdapter(List<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    // Métodos del adaptador

    @NonNull
    @Override
    public AiChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AiChatAdapter.ChatViewHolder holder, int position) {
        ChatMessage chat = chatList.get(position);

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
