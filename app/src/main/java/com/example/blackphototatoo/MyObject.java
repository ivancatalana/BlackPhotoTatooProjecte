package com.example.blackphototatoo;

public class MyObject {
    private int imageResource;
    private String text;

    public MyObject(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {
        return text;
    }
}
