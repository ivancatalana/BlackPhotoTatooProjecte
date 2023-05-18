package com.example.blackphototatoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;

public class EditPhotoFragment extends Fragment {

    private static final int REQUEST_CODE_CROP_PHOTO = 1;
    private ImageView photoImageView;
    private GPUImage gpuImage;
    private SeekBar brightnessSeekBar;
    private SeekBar contrastSeekBar;
    private Button effects;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);

        photoImageView = view.findViewById(R.id.photo_image_view);
        gpuImage = new GPUImage(getContext());
        brightnessSeekBar = view.findViewById(R.id.brightnessSeekBar1);
        contrastSeekBar = view.findViewById(R.id.contrastSeekBar1);
        ImageView cropButton = view.findViewById(R.id.cropButton);
        cropButton.setOnClickListener(v -> {
            // Obtén el Bitmap de la imagen editada
            Bitmap editedBitmap = gpuImage.getBitmapWithFilterApplied();

            // Crea un Intent para el recorte de la imagen
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            // Establece el tipo de datos y la imagen de origen para el recorte
            cropIntent.setDataAndType(getImageUri(getContext(), editedBitmap), "image/*");

            // Configura los extras para el recorte
            cropIntent.putExtra("crop", "true");
            int desiredWidth = 500; // Ancho deseado
            int desiredHeight = 300; // Alto deseado

            cropIntent.putExtra("outputX", desiredWidth);
            cropIntent.putExtra("outputY", desiredHeight);
            cropIntent.putExtra("scale", true);

            cropIntent.putExtra("return-data", true);

            // Inicia la actividad de recorte con el resultado esperado
            startActivityForResult(cropIntent, REQUEST_CODE_CROP_PHOTO);
        });


        // Obtener la URI de la imagen seleccionada desde los argumentos
        Bundle args = getArguments();
        if (args != null && args.containsKey("selectedImageUri")) {
            Uri selectedImageUri = args.getParcelable("selectedImageUri");
            // Cargar la imagen en el ImageView utilizando la URI
            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        selectedImageUri
                );
                photoImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Configurar imagen inicial
        // Obtener el Drawable del archivo drawable
        Drawable drawable = photoImageView.getDrawable();

        // Convertir el Drawable en Bitmap
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Establecer el Bitmap en el GPUImageView
        gpuImage.setImage(bitmap);

        // Actualizar imagen al cambiar brillo
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                comprimirImagen(gpuImage.getBitmapWithFilterApplied());
            }
        });

        // Actualizar imagen al cambiar contraste
        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateContrast(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                comprimirImagen(gpuImage.getBitmapWithFilterApplied());
            }
        });

        return view;
    }
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    //-----------------------------------------------------------------------------------------------------------------//-----------------------------------------------------------------------------------------------------------------
    //---------------------------------------Metodes per editar la imatge-------------------------------------------------------------//-----------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------//-----------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------//-----------------------------------------------------------------------------------------------------------------

    private void updateBrightness(int brightness) {
        gpuImage.setFilter(new GPUImageBrightnessFilter(brightness / 100f));

        photoImageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
    }

    private void updateContrast(int contrast) {
        gpuImage.setFilter(new GPUImageContrastFilter(contrast / 100f));
        photoImageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
    }
    private void comprimirImagen(Bitmap bitmap) {
        new CompressImageTask(bitmap).execute();
    }

    private class CompressImageTask extends AsyncTask<Void, Void, Void> {

        private Bitmap bitmap;

        public CompressImageTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String fileName = "mi_imagen.jpg";
            FileOutputStream outputStream;
            try {
                outputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Aquí puedes hacer algo después de que la compresión esté completa
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CROP_PHOTO && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                // Obtén el bitmap recortado del intent
                Bitmap croppedBitmap = extras.getParcelable("data");

                // Carga el bitmap recortado en el ImageView
                photoImageView.setImageBitmap(croppedBitmap);

                // Aquí puedes realizar cualquier otra operación con el bitmap recortado si es necesario

                // Comprime la imagen si lo deseas
                comprimirImagen(croppedBitmap);
            }
        }
    }
}
