package com.example.blackphototatoo;

public class MyEmails {
    private int imageResource;
    private String text;
    private String fecha;
    private String name;


    public MyEmails(int imageResource, String name,String fecha, String text) {
        this.imageResource = imageResource;
        this.text = text;
        this.fecha=fecha;
        this.name=name;

    }

    public int getImageResource() {
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
}
