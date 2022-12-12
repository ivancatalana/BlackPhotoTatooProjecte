package com.example.blackphototatoo;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

@Database(entities = { Elemento.class }, version = 1, exportSchema = false)

 public abstract class ElementosBaseDeDatos extends RoomDatabase {

    public abstract ElementosDao obtenerElementosDao();

    private static volatile ElementosBaseDeDatos INSTANCIA;

    static ElementosBaseDeDatos obtenerInstancia(final Context context) {
        if (INSTANCIA == null) {
            synchronized (ElementosBaseDeDatos.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(context,
                                    ElementosBaseDeDatos.class, "elementos.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCIA;
    }
    @Dao
    interface ElementosDao {
        @Query("SELECT * FROM Elemento")
        LiveData<List<Elemento>> obtener();

        @Insert
        void insertar(Elemento elemento);

        @Update
        void actualizar(Elemento elemento);

        @Delete
        void eliminar(Elemento elemento);
        @Query("SELECT * FROM Elemento ORDER BY valoracion DESC")
        LiveData<List<Elemento>> masValorados();

        @Query("SELECT * FROM Elemento WHERE nombre LIKE '%' || :t || '%'")
        LiveData<List<Elemento>> buscar(String t);

    }
}