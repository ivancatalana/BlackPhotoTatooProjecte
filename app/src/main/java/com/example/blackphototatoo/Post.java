package com.example.blackphototatoo;


import java.util.HashMap;
import java.util.Map;

public class Post {
    public String uid;
    public String author;
    public String dateTimePost;
    public String ordenadaDateTime;
    // public String date
    public String authorPhotoUrl;
    public String content;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateTimePost() {
        return dateTimePost;
    }

    public void setDateTimePost(String dateTimePost) {
        this.dateTimePost = dateTimePost;
    }

    public String getOrdenadaDateTime() {
        return ordenadaDateTime;
    }

    public void setOrdenadaDateTime(String ordenadaDateTime) {
        this.ordenadaDateTime = ordenadaDateTime;
    }

    public String getAuthorPhotoUrl() {
        return authorPhotoUrl;
    }

    public void setAuthorPhotoUrl(String authorPhotoUrl) {
        this.authorPhotoUrl = authorPhotoUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public String mediaUrl;
    public String mediaType;
    public Map<String, Boolean> likes = new HashMap<>();


    // Constructor vacio requerido por Firestore
    public Post() {}
    //new Post(user.getUid(), user.getDisplayName(), (user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "R.drawable.user"), postContent, mediaUrl, mediaTipo);

    public Post(String uid, String author, String dateTimePost,String ordenadaDateTime,  String authorPhotoUrl, String content,String mediaUrl, String mediaType) {
        this.uid = uid;
        this.author = author;
        this.dateTimePost=dateTimePost;
        this.ordenadaDateTime=ordenadaDateTime;
        this.authorPhotoUrl = authorPhotoUrl;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
/*
public class Post {
public String uid;
public String author;
public String authorPhotoUrl;
public String content;
public String mediaUrl;
public String mediaType;
public Map<String, Boolean> likes = new HashMap<>();
// Constructor vacio requerido por Firestore public Post() {}
public Post(String uid, String author, String authorPhotoUrl, String content, String mediaUrl, String mediaType) {
this.uid = uid;
this.author = author;
 this.authorPhotoUrl = authorPhotoUrl;
  this.content = content;
   this.mediaUrl = mediaUrl;
    this.mediaType = mediaType;
} }
 */
}