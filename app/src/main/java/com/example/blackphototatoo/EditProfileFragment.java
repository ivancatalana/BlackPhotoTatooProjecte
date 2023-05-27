package com.example.blackphototatoo;


import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditProfileFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_PHOTO = 1;

    private NavController navController;   // <-----------------
    private FirebaseAuth mAuth;
    private Button editName;
    private Button signOut;
    private Uri selectedImageUri;
    private View view;
    private PhotoView profileImageView;
    private String nombreUsuario;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View viewn, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view=viewn;
        mAuth=FirebaseAuth.getInstance();
        editName= view.findViewById(R.id.button9);
        signOut= view.findViewById(R.id.buttonSignOut);
        profileImageView = view.findViewById(R.id.friendProfileImageView);
        navController = Navigation.findNavController(view);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build()).signOut();
                FirebaseAuth.getInstance().signOut();
                // Cierra completamente la aplicación
                //System.out.println(mAuth.getCurrentUser().getEmail());
               navController.navigate(R.id.loginFragment);
                final Handler handler = new Handler(Looper.getMainLooper());

        //Temporizador para actualizar la variable antes de mostrarla (Si no se muestra a 0)

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                restartApplication(requireActivity());

            }
        }, 200);

            }
        });

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(  mAuth.getCurrentUser().getDisplayName()+"----------____________________________________________");
                showPopupName();
            }
        });
        view.findViewById(R.id.button20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupPass();
            }
        });
        view.findViewById(R.id.changePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
                       }
        });

        view.findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showPopup();
            }
        });


                NavController navController = Navigation.findNavController(view);

        // navegar a otro fragmento
        view.findViewById(R.id.button11).setOnClickListener(v -> {
            navController.navigate(R.id.action_blankFragment_to_bottom1Fragment);
        });

        // Cargar la imagen de perfil utilizando Glide y PhotoView
        if (selectedImageUri != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))) // Opcional: Aplicar esquinas redondeadas a la imagen
                    .into(profileImageView);
        }
    }

    private void showPopupName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.modal, null);
        builder.setView(view);
        EditText editText = view.findViewById(R.id.editText);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nombreUsuario = editText.getText().toString();
                cambiarNombre();
                // Aquí puedes guardar el texto ingresado en el EditText
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void showPopupPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.password, null);
        builder.setView(view);
        final EditText oldpass = view.findViewById(R.id.oldPass);
        final EditText newpass = view.findViewById(R.id.newPass);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes guardar el texto ingresado en el EditText
                String oldp = oldpass.getText().toString().trim();
                String newp = newpass.getText().toString().trim();
                cambiarContrasenya(oldp,newp);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void cambiarNombre(){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombreUsuario)
                .build();

        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User name updated.");
                            Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionRef = firestore.collection("usuariosPrueba");
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        com.google.firebase.firestore.Query query = collectionRef.whereEqualTo("mail", currentUser.getEmail());

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException exception) {
                if (exception != null) {
                    // Error al obtener los datos de Firestore
                    System.out.println("DATABASE error ---------------------------------------__---");
                    return;
                }

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Existe al menos un documento en la colección donde el valor del atributo "mail" es igual al correo del usuario actual
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        String documentId = documentSnapshot.getId();
                        System.out.println(documentSnapshot.get("name") + "----------------------------getter");

                        DocumentReference documentRef = collectionRef.document(documentId);
                        documentRef.update("name", nombreUsuario);
                    }
                } else {
                    // No existe ningún documento en la colección con el valor del atributo "mail" igual al correo del usuario actual
                    System.out.println("---------------------------------------------------------------------NO--EXISTE");
                }
            }
        });

    }
    private void cambiarContrasenya(String oldp,final String newp){
        final FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldp);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newp)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Changed Password", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void pujaIguardarEnFirestore(final Uri mediaUri, final FirebaseUser user ,  final Context context) {
        try {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Actualizando foto de perfil...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Glide.with(requireContext())
                    .load(mediaUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // Comprimir la imagen con calidad del 75%
                            byte[] imageData = baos.toByteArray();

                            final StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + UUID.randomUUID());
                            UploadTask uploadTask = storageRef.putBytes(imageData);
                            uploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw task.getException(); // Manejar cualquier error durante la carga de la imagen
                                }

                                // La imagen se ha cargado exitosamente, obtener la URL de descarga
                                return storageRef.getDownloadUrl();
                            }).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    // La URL de descarga se ha obtenido, guardarla en Firestore
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(downloadUri)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnSuccessListener(aVoid -> {
                                                // La foto de perfil se ha actualizado correctamente
                                                showToast(context,"Imagen cambiada correctamente");
                                                actualizarImagenUsuarios();
                                                progressDialog.dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Error al actualizar la foto de perfil
                                                showToast(context,"Error al actualizar la foto de perfil");
                                                progressDialog.dismiss();
                                            });
                                } else {
                                    // Error al obtener la URL de descarga
                                    showToast(context,"Error al obtener la URL de descarga");
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showToast(context,"Error al cargar y comprimir la imagen");
        }

    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            pujaIguardarEnFirestore( selectedImageUri, mAuth.getCurrentUser(), getContext());
        }
    }
    public void restartApplication(Activity activity) {
        Intent intent = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    public void actualizarImagenUsuarios(){
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference collectionRef = firestore.collection("usuariosPrueba");
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        com.google.firebase.firestore.Query query = collectionRef.whereEqualTo("mail", currentUser.getEmail());

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException exception) {
                if (exception != null) {
                    // Error al obtener los datos de Firestore
                    System.out.println("DATABASE error ---------------------------------------__---");
                    return;
                }

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Existe al menos un documento en la colección donde el valor del atributo "mail" es igual al correo del usuario actual
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        String documentId = documentSnapshot.getId();
                        DocumentReference documentRef = collectionRef.document(documentId);
                        documentRef.update("uidPhotoUrl", mAuth.getCurrentUser().getPhotoUrl());
                    }
                } else {
                    // No existe ningún documento en la colección con el valor del atributo "mail" igual al correo del usuario actual
                    System.out.println("---------------------------------------------------------------------NO--EXISTE");
                }
            }
        });
    }
}