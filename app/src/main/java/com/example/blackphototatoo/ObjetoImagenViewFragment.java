package com.example.blackphototatoo;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ObjetoImagenViewFragment extends Fragment {
    private RecyclerView recyclerView;

    public ObjetoImagenViewFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<MyObject> myObjects = new ArrayList<>();
        myObjects.add(new MyObject(R.drawable.e1, "Objeto 1"));
        myObjects.add(new MyObject(R.drawable.e2, "Objeto 2"));
        myObjects.add(new MyObject(R.drawable.e3, "Objeto 3"));
        myObjects.add(new MyObject(R.drawable.e4, "Objeto 4"));
        MyAdapter adapter = new MyAdapter(myObjects);
        recyclerView.setAdapter(adapter);
    }
}