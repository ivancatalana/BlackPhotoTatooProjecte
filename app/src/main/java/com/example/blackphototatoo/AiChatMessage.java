package com.example.blackphototatoo;

import android.net.Uri;

import java.util.Date;

public class AiChatMessage {
    private String prompt;
    private Uri imageUri;
    private String imageUrl;
    private Date date;

    public AiChatMessage(String prompt, Uri imageUri, String imageUrl,  Date date) {
        this.prompt = prompt;
        this.imageUri = imageUri;
        this.imageUrl = imageUrl;
        this.date = date;
    }



    public String getPrompt() {
        return prompt;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDate() {
        return date;
    }
}
