package com.example.blackphototatoo;

public class ObjetoImagen {
    private int imageResource;
    private String ranking;
    private String fecha;
    private String name;


    public ObjetoImagen(int imageResource, String name,String fecha, String ranking) {
        this.imageResource = imageResource;
        this.ranking = ranking;
        this.fecha=fecha;
        this.name=name;

    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {
        return ranking;
    }

    public String getFecha() {
        return fecha;
    }
    public String getName() {
        return name;
    }
}
