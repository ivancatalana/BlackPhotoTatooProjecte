package com.example.blackphototatoo;

import java.util.Date;

public class ChatMessage {
    private String content;
    private Date timestamp;
    private boolean isSender;
    private String userId;
    private String profileImageUrl;

    public ChatMessage() {
        // Constructor vacío requerido para Firestore
    }

    public ChatMessage(String content, Date timestamp, String userId, boolean isSender) {
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
        this.isSender = isSender;

    }

    public boolean isSender() {
        return isSender;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

