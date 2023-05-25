package com.example.blackphototatoo;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.Toast;
// Importa las clases

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageThresholdEdgeDetectionFilter;

public class EditPhotoFragment extends Fragment {

    private static final int REQUEST_CODE_CROP_PHOTO = 1;
    private ImageView photoImageView;
    private GPUImage gpuImage;
    private SeekBar brightnessSeekBar;
    private SeekBar contrastSeekBar;
    private Button effects;
    Bitmap fotoFinal;
    private Uri sourceUri;  // URI de la imagen original
    private Uri destinationUri;  // URI de destino para la imagen recortada
    private File sourceFile;
    private File destinationFile;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);

        photoImageView = view.findViewById(R.id.photo_image_view);
        gpuImage = new GPUImage(getContext());
        brightnessSeekBar = view.findViewById(R.id.brightnessSeekBar1);
        contrastSeekBar = view.findViewById(R.id.contrastSeekBar1);
        ImageView cropButton = view.findViewById(R.id.cropButton);
//        // Configurar OnClickListener para el botón de recorte
//        cropButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Drawable drawable = photoImageView.getDrawable();
//                Bitmap editedBitmap = ((BitmapDrawable) drawable).getBitmap();
//
//                int maxWidth = 800;
//                int maxHeight = 800;
//
//                sourceFile = createTempFile("source_image", ".png");
//                saveBitmapToFile(editedBitmap, sourceFile);
//
//                destinationFile = createTempFile("destination_image", ".png");
//
//                UCrop.Options options = new UCrop.Options();
//                options.setAspectRatioOptions(1, new AspectRatio("1:1", 1, 1));
//                options.setToolbarColor(Color.RED);
//                options.setStatusBarColor(Color.RED);
//                options.setActiveControlsWidgetColor(Color.RED);
//                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//
//                sourceUri = Uri.fromFile(sourceFile);
//                destinationUri = Uri.fromFile(destinationFile);
//
//                UCrop.of(sourceUri, destinationUri)
//                        .withOptions(options)
//                        .withMaxResultSize(maxWidth, maxHeight)
//                        .start(getActivity());
//
//                photoImageView.setDrawingCacheEnabled(false);
//            }
//        });

        cropButton.setOnClickListener(v -> {
            // Obtén el Drawable del archivo drawable
            Drawable drawable = photoImageView.getDrawable();

            // Convertir el Drawable en Bitmap
            Bitmap editedBitmap = ((BitmapDrawable) drawable).getBitmap();

            // Crea un archivo temporal para el archivo de origen
            File sourceFile = createTempFile("source_image", ".png");

            // Guarda el bitmap editado en el archivo de origen
            saveBitmapToFile(editedBitmap, sourceFile);

            // Crea un archivo temporal para el archivo de destino
            File destinationFile = createTempFile("destination_image", ".png");

            // Crea el Intent para el recorte de la imagen utilizando uCrop
            UCrop.Options options = new UCrop.Options();
            options.setAspectRatioOptions(1, new AspectRatio("1:1", 1, 1));
            options.setToolbarColor(Color.RED);
            options.setStatusBarColor(Color.RED);
            options.setActiveControlsWidgetColor(Color.RED);
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);

            UCrop.of(Uri.fromFile(sourceFile), Uri.fromFile(destinationFile))
                    .withOptions(options)
                    .start(getActivity(), this);  // Inicia la actividad de uCrop con el fragmento actual como resultado
        });
//        cropButton.setOnClickListener(v -> {
//            // Obtener el Drawable del archivo drawable
//            Drawable drawable = photoImageView.getDrawable();
//
//            // Convertir el Drawable en Bitmap
//            Bitmap editedBitmap = ((BitmapDrawable) drawable).getBitmap();
//
//            // Crea un Intent para el recorte de la imagen
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//
//            // Establece el tipo de datos y la imagen de origen para el recorte
//            cropIntent.setDataAndType(getImageUri(getContext(), editedBitmap), "image/*");
//
//            // Configura los extras para el recorte
//            cropIntent.putExtra("crop", "true");
//
//            // Establece el tamaño máximo deseado para la imagen recortada
//            int maxOutputSize = 800;
//
//            // Obtén la relación de aspecto original de la imagen
//            float originalAspectRatio = (float) editedBitmap.getWidth() / editedBitmap.getHeight();
//
//            int desiredWidth;
//            int desiredHeight;
//
//            if (editedBitmap.getWidth() > editedBitmap.getHeight()) {
//                // La imagen es más ancha que alta
//                desiredWidth = maxOutputSize;
//                desiredHeight = (int) (maxOutputSize / originalAspectRatio);
//            } else {
//                // La imagen es más alta que ancha o tiene la misma altura que anchura
//                desiredWidth = editedBitmap.getWidth();
//                desiredHeight = editedBitmap.getHeight();
//            }
//
//            cropIntent.putExtra("outputX", desiredWidth);
//            cropIntent.putExtra("outputY", desiredHeight);
//
//            cropIntent.putExtra("scale", true);
//            cropIntent.putExtra("return-data", true);
//
//            // Inicia la actividad de recorte con el resultado esperado
//            startActivityForResult(cropIntent, REQUEST_CODE_CROP_PHOTO);
//        });

        Button guardarButton = view.findViewById(R.id.guardarButton);
        guardarButton.setOnClickListener(v -> {
            guardarImagenEnGaleria();
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
        fotoFinal = bitmap;
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
        fotoFinal=gpuImage.getBitmapWithFilterApplied();
    }

    private void updateContrast(int contrast) {
        gpuImage.setFilter(new GPUImageContrastFilter(contrast / 100f));
        photoImageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
        fotoFinal=gpuImage.getBitmapWithFilterApplied();

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

            // Obtén el contexto actual
            Context context = getContext();

            // Elimina el archivo temporal
            String fileName = "mi_imagen.jpg";
            File file = new File(context.getFilesDir(), fileName);
            file.delete();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // Obtiene la URI de la imagen recortada
            final Uri resultUri = UCrop.getOutput(data);

            // Carga la imagen recortada en el ImageView
            photoImageView.setImageURI(resultUri);

            // Borra los archivos temporales
            if (sourceFile != null) {
                sourceFile.delete();
            }
            if (destinationFile != null) {
                destinationFile.delete();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle error case
            final Throwable cropError = UCrop.getError(data);
            // Maneja el error de recorte
        }
    }


    private File createTempFile(String prefix, String extension) {
        try {
            File tempDir = getActivity().getCacheDir();
            return File.createTempFile(prefix, extension, tempDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File saveBitmapToTempFile(Bitmap bitmap) {
        try {
            File tempDir = requireActivity().getCacheDir();
            File tempFile = File.createTempFile("temp_image", ".jpg", tempDir);

            OutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void guardarImagenEnGaleria() {
        // Obtén el Drawable del ImageView

        Drawable drawable = photoImageView.getDrawable();

        // Convierte el Drawable en Bitmap
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Crea un nuevo Bitmap para guardar la imagen editada
        Bitmap editedBitmap = Bitmap.createBitmap(bitmap);

        // Guarda la imagen en la galería
        String imageFileName = "mi_imagen.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        ContentResolver resolver = requireActivity().getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outputStream;
        try {
            outputStream = resolver.openOutputStream(imageUri);
            editedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            // Escanea y agrega el archivo al MediaStore
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(imageUri);
            requireActivity().sendBroadcast(mediaScanIntent);

            Toast.makeText(requireContext(), "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
