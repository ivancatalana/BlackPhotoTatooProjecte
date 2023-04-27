package com.example.blackphototatoo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Tabbed1AFragment extends Fragment {
    private RecyclerView recyclerView;



    public Tabbed1AFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tabbed1_a, container, false);

        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        List<ObjetoImagen> myObjects = new ArrayList<>();
        myObjects.add(new ObjetoImagen(R.drawable.negracuadrado, "Imagen #1","23-2-2023  12:44","Not Uploaded"));
        myObjects.add(new ObjetoImagen(R.drawable.adri_, "Imagen #2","23-2-2023  12:44","☆☆☆☆☆"));
        myObjects.add(new ObjetoImagen(R.drawable.siablanconegro, "Imagen #3","23-2-2023  12:44","☆☆☆☆"));
        myObjects.add(new ObjetoImagen(R.drawable.barbas_, "Imagen #4","23-2-2023  12:44","☆☆☆☆☆"));

        //myObjects.add(new ObjetoImagen(R.drawable.barbas, "Imagen #5","23-2-2023  12:44","☆☆☆"));
        ObjetoImagenAdapter adapter = new ObjetoImagenAdapter(myObjects);
        recyclerView.setAdapter(adapter);


 */

        /*
        List<MyObject> myObjects = new ArrayList<>();
        myObjects.add(new MyObject(R.drawable.e5, "Objeto 1"));
        myObjects.add(new MyObject(R.drawable.e2, "Objeto 2"));
        myObjects.add(new MyObject(R.drawable.e3, "Objeto 3"));
        myObjects.add(new MyObject(R.drawable.e4, "Objeto 4"));
        myObjects.add(new MyObject(R.drawable.e1, "Objeto 5"));
        MyAdapter adapter = new MyAdapter(myObjects);
        recyclerView.setAdapter(adapter);

         */
    }
}