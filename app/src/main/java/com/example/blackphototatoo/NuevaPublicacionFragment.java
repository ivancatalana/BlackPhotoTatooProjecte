package com.example.blackphototatoo;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NuevaPublicacionFragment extends Fragment{

    NavController navController;   // <-----------------

    public AppViewModel appViewModel;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    Button publishButton;
    EditText postConentEditText;
    String mediaTipo;
    Uri mediaUri;
    String postDateAndTime;
    String dateTimeOrdenada;
    String postName;


// Define los patrones de formato de fecha
    SimpleDateFormat formatoPost = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
    SimpleDateFormat fechaOrdenada = new SimpleDateFormat("yyMMddHHmmss");


        public NuevaPublicacionFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_prueba_nuevo_post, container, false);
        }



        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            navController = Navigation.findNavController(view);
            super.onViewCreated(view, savedInstanceState);
            publishButton = view.findViewById(R.id.publishButton);
            postConentEditText = view.findViewById(R.id.postContentEditText);
            publishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    publicar();
                }
            });
            ImageView postIconImageView = view.findViewById(R.id.postsProfileImageView);
            postIconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                    } else {
                        pickImageFromGallery();
                    }
                }
            });
            //Añadimos La referencia del appviewModel que declaramos arriba Y losListeners para los botones de subir multimedia

            appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

            view.findViewById(R.id.camara_fotos).setOnClickListener(v -> tomarFoto());

            view.findViewById(R.id.imagen_galeria).setOnClickListener(v -> seleccionarImagen());
            appViewModel.mediaSeleccionado.observe(getViewLifecycleOwner(), media -> {
                this.mediaUri = media.uri;
                this.mediaTipo = media.tipo;
                Glide.with(this).load(media.uri).into((ImageView) view.findViewById(R.id.previsualizacion));
            });
            appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);


        }
          //PARTE de Storage añadida

        private void publicar() {
            String postContent = postConentEditText.getText().toString();
            if(TextUtils.isEmpty(postContent)){ postConentEditText.setError("Required"); return;
            }
            publishButton.setEnabled(false);

            if (mediaTipo == null) { guardarEnFirestore(postContent, null);
            } else {
                pujaIguardarEnFirestore(postContent); }
        }
        private void guardarEnFirestore(String postContent, String mediaUrl) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            System.out.println(user.toString());
            System.out.println("---------------------------------------------------------");
//            System.out.println(user.getPhotoUrl().toString());
            // Obtén la fecha actual
            Date currentDate = new Date();

            // Formatea la fecha según los patrones definidos
            postDateAndTime = formatoPost.format(currentDate);
            dateTimeOrdenada = fechaOrdenada.format(currentDate);
            System.out.println(postContent);
            if(user.getDisplayName()!=null){
                postName=user.getDisplayName();
            }
            else {
                postName = user.getEmail();
            }
            Post post = new Post(user.getUid(), postName,  postDateAndTime, dateTimeOrdenada, user.getPhotoUrl().toString(),postContent, mediaUrl, mediaTipo);

            FirebaseFirestore.getInstance().collection("posts")
                    .add(post)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            navController.popBackStack();
                            //   InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            // imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            appViewModel.setMediaSeleccionado(null,null);
                        }
                    });
        }
    private void pujaIguardarEnFirestore(final String postText) {
        try {
            // Comprimir la imagen antes de subirla
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), mediaUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // Comprimir la imagen con calidad del 75%
            byte[] imageData = baos.toByteArray();

            // Subir la imagen comprimida a Firebase Storage
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference(mediaTipo + "/" + UUID.randomUUID());
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
                    guardarEnFirestore(postText, downloadUri.toString());
                } else {
                    // Manejar el error durante la obtención de la URL de descarga
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar el error de compresión de imagen
        }
    }
        private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            appViewModel.setMediaSeleccionado(uri, mediaTipo); });
    private final ActivityResultLauncher<Uri> camaraFotos = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
        appViewModel.setMediaSeleccionado(mediaUri, "image");
    });
        private void seleccionarImagen() {
            mediaTipo = "image";
            galeria.launch("image/*");

        }  private void tomarFoto() { try {
        mediaUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", File.createTempFile("img", ".jpg", requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        camaraFotos.launch(mediaUri); } catch (IOException e) {}
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                appViewModel.setMediaSeleccionado(uri, "image");

            }
        }
    }

}


