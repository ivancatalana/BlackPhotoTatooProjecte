package com.example.blackphototatoo;


import static android.app.Activity.RESULT_OK;
import static android.view.View.getDefaultSize;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import io.grpc.Compressor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_PHOTO = 1;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private Button editName;
    private Uri selectedImageUri;
    private View view;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View viewn, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view=viewn;
        mAuth=FirebaseAuth.getInstance();
        editName= view.findViewById(R.id.button9);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        view.findViewById(R.id.button20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        view.findViewById(R.id.changePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
                       }
        });
        view.findViewById(R.id.button22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();            }
        });
        view.findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

                NavController navController = Navigation.findNavController(view);

        // navegar a otro fragmento
        view.findViewById(R.id.button11).setOnClickListener(v -> {
            navController.navigate(R.id.action_blankFragment_to_bottom1Fragment);
        });
    }
    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.modal, null);
        builder.setView(view);
        EditText editText = view.findViewById(R.id.editText);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    public static void pujaIguardarEnFirestore(final Uri mediaUri, final FirebaseUser user, final Context context) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(mediaUri)
                    .apply(new RequestOptions().override(800, 800)) // Establecer el tamaño máximo permitido
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // Comprimir la imagen con calidad del 75%
                            byte[] imageData = baos.toByteArray();

                            final StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + UUID.randomUUID());
                            UploadTask uploadTask = storageRef.putBytes(imageData);
                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Actualizando foto de perfil...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
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
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(user.getUid())
                                            .set(new HashMap<>())
                                            .addOnSuccessListener(aVoid -> {
                                                // El documento se ha creado correctamente

                                                // Actualizar la foto de perfil del usuario
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setPhotoUri(downloadUri)
                                                        .build();

                                                user.updateProfile(profileUpdates)
                                                        .addOnSuccessListener(aVoid1 -> {
                                                            // La foto de perfil se ha actualizado correctamente
                                                            showToast(context, "Imagen guardada correctamente");
                                                            progressDialog.dismiss();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Error al actualizar la foto de perfil
                                                            showToast(context, "Error al actualizar la foto de perfil");
                                                            progressDialog.dismiss();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                // Error al crear el documento
                                                showToast(context, "Error al crear el documento en Firestore");
                                            });
                                } else {
                                    // Error al obtener la URL de descarga
                                    showToast(context, "Error al obtener la URL de descarga");
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showToast(context, "Error al cargar y comprimir la imagen");
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
}