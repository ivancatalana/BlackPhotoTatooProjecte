package com.example.blackphototatoo;

public class Coordenadas {
    private double latitud;
    private double longitud;

    private String name;

    public Coordenadas() {
        // Constructor vacío requerido para Firebase
    }

    public Coordenadas(double latitud, double longitud, String name) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.name = name;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

