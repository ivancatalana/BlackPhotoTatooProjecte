package com.example.blackphototatoo;

public class MyEmails {
    private String imageResource;
    private String text;
    private String fecha;
    private String name;
    private String sender;
    private String receiver;


    public MyEmails(String imageResource, String name, String fecha, String text, String receiver, String sender) {
        this.imageResource = imageResource;
        this.text = text;
        this.fecha=fecha;
        this.name=name;
        this.receiver=receiver;
        this.sender = sender;

    }
    public String getSender() {
        return sender;
    }


    public String getImageResource() {
        return imageResource;
    }

    public String getText() {
        return text;
    }

    public String getFecha() {
        return fecha;
    }
    public String getName() {
        return name;
    }
    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


}
