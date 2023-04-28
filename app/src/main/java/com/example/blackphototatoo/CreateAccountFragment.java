package com.example.blackphototatoo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountFragment extends Fragment {
    Button botonCrearCuenta;
    private EditText emailEditText, passwordEditText,retypepasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    NavController navController;
    public CreateAccountFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        botonCrearCuenta=view.findViewById(R.id.buttonCreate);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        retypepasswordEditText = view.findViewById(R.id.retypepassword);
        mAuth = FirebaseAuth.getInstance();
        botonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {crearCuenta();
            }
        });
    }
    private void crearCuenta() {
        if (!validarFormulario()) {
            return;
        }

        botonCrearCuenta.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            actualizarUI(mAuth.getCurrentUser());
                        } else {
                            Snackbar.make(requireView(), "Error: " + task.getException(), Snackbar.LENGTH_LONG).show();

                        }
                        botonCrearCuenta.setEnabled(true);
                    }
                });

    }

    private void actualizarUI(FirebaseUser currentUser) {
        if(currentUser != null){
            navController.navigate(R.id.action_createAccountFragment_to_loginFragment);
        }
    }

    private boolean validarFormulario() {
        boolean valid = true;

        if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }
}