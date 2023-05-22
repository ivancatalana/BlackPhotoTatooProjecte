package com.example.blackphototatoo;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class DiscoverFragment extends Fragment {
    private NavController navController;
    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Evita volver atras

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //  Handle the back button even
                // Aqui podemos configurar el comprotamiento del boton back
                Log.d("BACKBUTTON", "Back button clicks");
            }
        };

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
        navController = Navigation.findNavController(view);
//        List<MyObject> myObjects = new ArrayList<>();
//        myObjects.add(new MyObject(R.drawable.e5, "Objeto 1"));
//        myObjects.add(new MyObject(R.drawable.e2, "Objeto 2"));
//        myObjects.add(new MyObject(R.drawable.e3, "Objeto 3"));
//        myObjects.add(new MyObject(R.drawable.e4, "Objeto 4"));
//        myObjects.add(new MyObject(R.drawable.e1, "Objeto 5"));
//        MyAdapter adapter = new MyAdapter(myObjects);
//        adapter.setNavController(navController);
//        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_state");
            try {

//                postsRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            catch ( NullPointerException n ){
                System.out.println("error");
            }
        }


        // navegar a otro fragmento
        view.findViewById(R.id.button15).setOnClickListener(v -> {
            navController.navigate(R.id.searchFragment);
        });


    }
}