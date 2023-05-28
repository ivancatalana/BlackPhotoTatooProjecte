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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateAccountFragment extends Fragment {
    private Button botonCrearCuenta;
    private EditText emailEditText, passwordEditText, retypepasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private NavController navController;
    final boolean[] isPremium = {false}; // Declarar como arreglo de un solo elemento

    public CreateAccountFragment() {
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
        botonCrearCuenta = view.findViewById(R.id.buttonCreate);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        retypepasswordEditText = view.findViewById(R.id.retypepassword);
        mAuth = FirebaseAuth.getInstance();

        CheckBox premiumCheckBox = view.findViewById(R.id.premiumCheckBox);
        premiumCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPremium[0] = isChecked; // Acceder al valor a través del arreglo
            }
        });



        botonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearCuenta();
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
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference profileRef = storage.getReference().child("profileImages/").child("61263271-cuenta-de-usuario-perfil-del-icono-del-círculo-plana-para-aplicaciones-y-sitios-web.jpg");

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mAuth.getCurrentUser().getEmail())
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "User profile updated.");
//                                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            final DatabaseReference databaser = FirebaseDatabase.getInstance().getReference("usuariosPrueba");
//                            Query query = databaser.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
//                            query.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                                        String child = databaser.getKey();
//                                        dataSnapshot1.getRef().child("name").setValue(mAuth.getCurrentUser().getEmail());
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });

                           // profileRef.getDownloadUrl();
                            System.out.println(mAuth.getCurrentUser().getDisplayName());
                            Users u = new Users("https://firebasestorage.googleapis.com/v0/b/blackphototatoo.appspot.com/o/profileImages%2F838c756f-ab57-469d-beb0-93594c6557e4?alt=media&token=4e5f687a-9e5c-4f9b-88b1-76231dd36314", mAuth.getUid(), mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getEmail(),isPremium[0]);
                            FirebaseFirestore.getInstance().collection("usuariosPrueba").add(u);
                        } else {
                            Snackbar.make(requireView(), "Error: " + task.getException(), Snackbar.LENGTH_LONG).show();

                        }
                        botonCrearCuenta.setEnabled(true);
                    }
                });

    }

    private void actualizarUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            navController.navigate(R.id.discoverFragment);
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