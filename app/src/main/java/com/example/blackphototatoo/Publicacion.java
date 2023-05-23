package com.example.blackphototatoo;

import java.util.HashMap;
import java.util.Map;

public class Publicacion {



        public String uid;
        public String author;
        public String dateTimePost;
        public String ordenadaDateTime;
        public String nombre;
        public String authorPhotoUrl;
        public String mediaUrl;
        public String mediaType;
        public Map<String, Boolean> likes = new HashMap<>();


        // Constructor vacio requerido por Firestore
        public Publicacion() {}
        //new Post(user.getUid(), user.getDisplayName(), (user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "R.drawable.user"), postContent, mediaUrl, mediaTipo);

        public Publicacion(String uid, String author, String dateTimePost,String ordenadaDateTime,  String authorPhotoUrl, String content,String mediaUrl, String mediaType) {
            this.uid = uid;
            this.author = author;
            this.dateTimePost=dateTimePost;
            this.ordenadaDateTime=ordenadaDateTime;
            this.authorPhotoUrl = authorPhotoUrl;
            this.nombre = content;
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
