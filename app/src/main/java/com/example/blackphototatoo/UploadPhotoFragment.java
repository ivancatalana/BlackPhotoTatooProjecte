package com.example.blackphototatoo;

import static android.app.Activity.RESULT_OK;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

public class UploadPhotoFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_PHOTO = 1;

    private Uri selectedImageUri;
    private NavController navController;
    private View view;
    private Button effectsButton;
    private String mediaTipo;
    private ImageView selectedPhotoThumbnail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Evita volver atras al splashScreen

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
        view = inflater.inflate(R.layout.fragment_upload_photo, container, false);
        effectsButton = view.findViewById(R.id.effectButton);
        selectedPhotoThumbnail = view.findViewById(R.id.selected_photo_thumbnail);
        Button selectPhotoButton = view.findViewById(R.id.select_photo_button);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
            }
        });
        selectedPhotoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica a ejecutar cuando se hace clic en el ImageView
                seleccionarImagen();            }
        });


        Button nuevaPublicacionButton = view.findViewById(R.id.nuevaPublicacionButton);
        nuevaPublicacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.nuevaPublicacionFragment);

            }
        });
       Button chatIA = view.findViewById(R.id.chatButton);
        chatIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedImageUri != null) {
                    Bundle args = new Bundle();
                    args.putParcelable("selectedImageUri", selectedImageUri);
                    navController.navigate(R.id.editor_ai_Fragment, args);
                } else {
                    Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button chatList = view.findViewById(R.id.chatButtonPrueba);
        chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.chatListFragment);

            }
        });
      Button chatIAMessages = view.findViewById(R.id.buttonAiMessages);
        chatIAMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.aiChatFragment);



            }
        });
        Button discFragment = view.findViewById(R.id.discoverButton);
        discFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.discoverFirebaseFragment);

            }
        });

        Button editSelected = view.findViewById(R.id.editSelectedButton);
        editSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                   // navController.navigate(R.id.);
                } else {
                    Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        effectsButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                Bundle args = new Bundle();
                args.putParcelable("selectedImageUri", selectedImageUri);
                navController.navigate(R.id.action_uploadFragment_to_editPhotoFragment, args);
            } else {
                Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        // Establecer el NavController en la vista
        Navigation.setViewNavController(view, navController);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Obtener la miniatura de la imagen seleccionada
            Bitmap thumbnail = null;
            try {
//                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
//                        getActivity().getContentResolver(),
//                        Long.parseLong(selectedImageUri.getLastPathSegment()),
//                        MediaStore.Images.Thumbnails.MINI_KIND,
//                        null
 //               );
                Glide.with(requireContext())
                        .load(selectedImageUri)
                        .into(selectedPhotoThumbnail);
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar la miniatura de la imagen", e);
            }

            // Mostrar la miniatura en la ImageView
           // selectedPhotoThumbnail.setImageBitmap(thumbnail);
        }
    }
    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        selectedImageUri = uri;
        Glide.with(requireContext())
                .load(selectedImageUri)
                .into(selectedPhotoThumbnail);
         });
    private void seleccionarImagen() {
        mediaTipo = "image";
        galeria.launch("image/*");
    }

}
