package com.example.blackphototatoo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginFragment extends Fragment {

    Button botonSiguiente;
    Button botonCrearCuenta;

    NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        botonSiguiente = view.findViewById(R.id.ingresar);
        botonCrearCuenta=view.findViewById(R.id.crearCuenta);

        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_blankFragment);
            }
        });
        botonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_createAccountFragment);
            }
        });
    }
}