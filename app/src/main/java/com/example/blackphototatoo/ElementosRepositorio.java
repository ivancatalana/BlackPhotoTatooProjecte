package com.example.blackphototatoo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ElementosRepositorio {
    ElementosBaseDeDatos.ElementosDao elementosDao;
    Executor executor = Executors.newSingleThreadExecutor();

    ElementosRepositorio(Application application){
        elementosDao = ElementosBaseDeDatos.obtenerInstancia(application).obtenerElementosDao();
    }
    LiveData<List<Elemento>> obtener(){
        return elementosDao.obtener();
    }



   // ElementosRepositorio(){
        /*
        elementos.add(new Elemento("@LoloMoMi", "Es un átomo con moléculas, aquella sustancia que no puede ser descompuesta mediante una reacción química, en otras más uno de sus isótopos.",R.drawable.e5));
        elementos.add(new Elemento("@SantaKlouSS", "En teoría de conjuntos, un elemento o miembro de un conjunto (o familia de conjuntos) es un objeto que forma parte de ese conjunto (o familia).",R.drawable.e2));
        elementos.add(new Elemento("@PepPol", "En química, un elemento sintético es un elemento químico que no aparece de forma natural en la Tierra, y solo puede ser creado artificialmente.",R.drawable.e3));
        elementos.add(new Elemento("@AsucarrMoreno", "En matemáticas, más concretamente en álgebra abstracta y teoría de cuerpos, se dice que un elemento es algebraico sobre un cuerpo si es raíz de algún polinomio con coeficientes en dicho cuerpo. Los elementos algebraicos sobre el cuerpo de los números racionales reciben el nombre de números algebraicos.",R.drawable.e4));
        elementos.add(new Elemento("@robertLewan","Lo malo y aire, a los que se añade la quintaesencia  muchas doctrinas antiguasia y explicaban el  de la naturaleza. El modelo estuvo vigente hasta que la ciencia moderna empezó a desentrañar los elementos y reacciones químicas.",R.drawable.e1));
        elementos.add(new Elemento("@BobPop","Un dia suelen  en y compartimentadores.",R.drawable.e2));

         */
    //}




    void insertar(Elemento elemento){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                elementosDao.insertar(elemento);
            }
        });
    }

    void eliminar(Elemento elemento) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                elementosDao.eliminar(elemento);
            }
        });
    }

    public void actualizar(Elemento elemento, float valoracion) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                elemento.valoracion = valoracion;
                elementosDao.actualizar(elemento);
            }
        });
    }
    LiveData<List<Elemento>> masValorados() {
        return elementosDao.masValorados();
    }

    LiveData<List<Elemento>> buscar(String t) {
        return elementosDao.buscar(t);
    }
}
