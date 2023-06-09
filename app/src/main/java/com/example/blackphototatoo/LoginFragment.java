package com.example.blackphototatoo;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {

    private Button botonSiguiente;
    private Button botonCrearCuenta;
    private SignInButton googleSignInButton;
    private EditText emailEditText, passwordEditText;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private FirebaseAuth mAuth;
    NavController navController;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
      //  System.out.println(mAuth.getCurrentUser().getEmail()+"--------------------------------------------------------------------------");
        navController = Navigation.findNavController(view);
        botonSiguiente = view.findViewById(R.id.ingresar);
        botonCrearCuenta=view.findViewById(R.id.crearCuenta);
        googleSignInButton = view.findViewById(R.id. googleSignInButton);
        emailEditText = view.findViewById(R.id.email);
        if(mAuth.getCurrentUser()!=null)navController.navigate(R.id.discoverFragment);
        passwordEditText = view.findViewById(R.id.password);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) { // There are no request codes
                            Intent data = result.getData();
                            try {
                                firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class));
                            } catch (ApiException e) {
                                Log.e("ABCD", "signInResult:failed code=" +
                                        e.getStatusCode());
                            }
                        }
                    }
                });
        mAuth = FirebaseAuth.getInstance();
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accederConGoogle();
            }
        });

        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailEditText.getText().toString().equals("")&& passwordEditText.getText().toString().equals(""))accederDirecto();
                else accederConEmail();
            }
        });
        botonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_createAccountFragment);
            }
        });
    }

    private void accederDirecto() {
        mAuth.signInWithEmailAndPassword("ivanmm@gmail.com", "123456")
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            actualizarUI(mAuth.getCurrentUser());
                        } else {
                            Snackbar.make(requireView(), "Error: " + task.getException(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------
    //----------Acceder con email--------------------------------------Acceder con email----------------------------------Acceder con email-----------------------
    //---------------------------------------------------------------------------------------------------------------------------------------
    private void accederConEmail() {

        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            actualizarUI(mAuth.getCurrentUser());
                        } else {
                            Snackbar.make(requireView(), "Error: " + task.getException(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------
    //---------Acceder con Google--------------------------------------Acceder con Google----------------------------------Acceder con Google-----------------------
    //---------------------------------------------------------------------------------------------------------------------------------------

    private void accederConGoogle() {
        GoogleSignInClient googleSignInClient =
                GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                        .build());
        activityResultLauncher.launch(googleSignInClient.getSignInIntent());
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if (acct == null) return;
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("ABCD", "signInWithCredential:success");

                           //verificarUsuarioEnDatabase(mAuth);
                           verify(mAuth.getCurrentUser());

                            actualizarUI(mAuth.getCurrentUser());
                            //verificarUsuarioEnDatabase(mAuth.getCurrentUser());
                        } else {
                            Log.e("ABCD", "signInWithCredential:failure",
                                    task.getException());
                        }
                    }
                });
    }

    private void registrarUserBBDD(FirebaseAuth mAuth) {

        String uid = mAuth.getCurrentUser().getUid();
        String name = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();
        String photoUrl = mAuth.getCurrentUser().getPhotoUrl().toString();

        Users newUser = new Users(photoUrl, uid, name, email,false);

        FirebaseFirestore.getInstance().collection("usuariosPrueba").add(newUser);
    }


    private void actualizarUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            navController.navigate(R.id.discoverFragment);
        }
    }
    public void verify(FirebaseUser user){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("usuariosPrueba");

        String emailToCheck = user.getEmail();
        com.google.firebase.firestore.Query query = collectionRef.whereEqualTo("mail", emailToCheck);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Existe al menos un documento en la colección donde el valor del atributo "email" es igual a "emailToCheck"
                        // Puedes realizar acciones adicionales aquí
                        System.out.println("-----------------------------------------------------------------------EXISTE");
                    } else {
                        // No existe ningún documento en la colección con el valor del atributo "email" igual a "emailToCheck"
                        // Puedes realizar acciones adicionales aquí
                        System.out.println("---------------------------------------------------------------------NO--EXISTE");
                        registrarUserBBDD(mAuth);
                    }
                } else {
                    // Error al obtener los datos de Firestore
                    Exception exception = task.getException();

                }

            }
        });
    }

}