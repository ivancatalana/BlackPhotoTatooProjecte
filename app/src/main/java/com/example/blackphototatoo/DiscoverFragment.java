package com.example.blackphototatoo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {
    protected ElementosViewModel elementosViewModel;
    private NavController navController;
    private RecyclerView recyclerView;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DiscoverFragment.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discover, container, false);
        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<MyObject> myObjects = new ArrayList<>();
        myObjects.add(new MyObject(R.drawable.e5, "Objeto 1"));
        myObjects.add(new MyObject(R.drawable.e2, "Objeto 2"));
        myObjects.add(new MyObject(R.drawable.e3, "Objeto 3"));
        myObjects.add(new MyObject(R.drawable.e4, "Objeto 4"));
        myObjects.add(new MyObject(R.drawable.e1, "Objeto 5"));
        MyAdapter adapter = new MyAdapter(myObjects);
        recyclerView.setAdapter(adapter);
    }
}