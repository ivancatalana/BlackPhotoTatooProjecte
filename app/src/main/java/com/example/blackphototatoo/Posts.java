package com.example.blackphototatoo;

import java.util.HashMap;
import java.util.Map;

public class Posts {
    private String authorPhotoUrl;
    private String content;
    public String mediaUrl;
    public String mediaType;
    public String uid;
    public String author;
    public Map<String, Boolean> likes = new HashMap<>();
    public String dateTimePost;
    public String ordenadaDateTime;
    public Posts() {}
    public Posts(String uid, String author, String dateTimePost,String ordenadaDateTime,  String authorPhotoUrl, String content,String mediaUrl, String mediaType) {
        this.uid = uid;
        this.author = author;
        this.dateTimePost=dateTimePost;
        this.ordenadaDateTime=ordenadaDateTime;
        this.authorPhotoUrl = authorPhotoUrl;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }

}
