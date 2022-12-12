package com.example.blackphototatoo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.concurrent.ThreadLocalRandom;

@Entity

public class Elemento {
    @PrimaryKey(autoGenerate = true)
    int id;
    String nombre;
    int imagen=caseRandomImage();
    String descripcion;
    float valoracion;

    public Elemento(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }



    /*
    public Elemento(String nombre, String descripcion, int imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }
     */
    static int caseRandomImage(){
        int random= ThreadLocalRandom.current().nextInt(1, 5);
        int imageRandom=0;

        switch (random) {
            case 1:
                imageRandom = R.drawable.e1;
                break;
            case 2:
                imageRandom = R.drawable.e2;
                break;
            case 3:
                imageRandom = R.drawable.e3;
                break;
            case 4:
                imageRandom = R.drawable.e4;
                break;
            case 5:
                imageRandom = R.drawable.e5;
                break;
        }
                return imageRandom;


        }

}


