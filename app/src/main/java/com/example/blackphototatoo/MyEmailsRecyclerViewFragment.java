package com.example.blackphototatoo;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyEmailsRecyclerViewFragment extends Fragment {
    private RecyclerView recyclerView;
    private NavController navController;

    public MyEmailsRecyclerViewFragment() {
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
        List<MyEmails> myObjects = new ArrayList<>();
        navController = Navigation.findNavController(view);
        myObjects.add(new MyEmails(R.drawable.users1, "Wendy Burguer", "31-10-2023  08:57", "Thanks, that was very helpful. "));
        myObjects.add(new MyEmails(R.drawable.users2, "James Stewart", "30-10-2023  21:27", "Hey! Awesome image! How did you do it?"));
        myObjects.add(new MyEmails(R.drawable.users3, "Pastanaga Guy", "30-10-2023  21:20", "Pues muy bien lo estuvimos haciendo"));
        myObjects.add(new MyEmails(R.drawable.profile_image, "Ivan Morales", "30-10-2023  18:20", "Los ipsilium meta alpha casi 1"));
        MyAdapterEmails adapter = new MyAdapterEmails(myObjects);
        recyclerView.setAdapter(adapter);
    }
}