package com.example.blackphototatoo;

import java.util.Date;

public class ChatMessage {
    private String content;
    private Date timestamp;

    public ChatMessage() {
        // Constructor vacío requerido para Firestore
    }

    public ChatMessage(String content, Date timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

