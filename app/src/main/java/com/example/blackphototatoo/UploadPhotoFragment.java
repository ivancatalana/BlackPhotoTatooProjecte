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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class UploadPhotoFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_PHOTO = 1;

    private Uri selectedImageUri;
    NavController navController;
    View view;
    private Button effectsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upload_photo, container, false);
        effectsButton = view.findViewById(R.id.effectButton);

        Button selectPhotoButton = view.findViewById(R.id.select_photo_button);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
            }
        });

        Button switchToBottom2Button = view.findViewById(R.id.switch_to_bottom2_button);
        switchToBottom2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    /*
                    EditFragment bottom2Fragment = new EditFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("selectedImageUri", selectedImageUri);
                    bottom2Fragment.setArguments(args);

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.uploadPhotoFragment, bottom2Fragment);
                    //fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                     */
                    navController.navigate(R.id.action_uploadPhotoFragment_to_bottom2Fragment);

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
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        getActivity().getContentResolver(),
                        Long.parseLong(selectedImageUri.getLastPathSegment()),
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null
                );
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar la miniatura de la imagen", e);
            }

            // Mostrar la miniatura en la ImageView
            ImageView selectedPhotoThumbnail = view.findViewById(R.id.selected_photo_thumbnail);
            selectedPhotoThumbnail.setImageBitmap(thumbnail);
        }
    }


}
