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
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
// Importa las clases

import com.google.android.material.navigation.NavigationView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceThresholdFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageThresholdEdgeDetectionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter;

public class EditPhotoFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_CROP_PHOTO = 1;
    private ImageView photoImageView;
    private GPUImage gpuImage;
    private SeekBar brightnessSeekBar;
    private SeekBar adjustSeekBar;
    private Button effects;
    Bitmap fotoFinal;
    private Uri sourceUri;  // URI de la imagen original
    private Uri destinationUri;  // URI de destino para la imagen recortada
    private File sourceFile;
    private File destinationFile;
    private DrawerLayout drawerLayout;
    private int lastSeekValue = 0;
    private TextView seekBarValueTextView;
    private Bitmap bitmap;
    private Bitmap originalBitmap;
    private GPUImageFilter currentFilter;
    private ConstraintLayout filt;
    private ConstraintLayout seekBarConstraint;
    private Button posterFilterButton;
    private Button filterSketch;
    private Button grayButton;
    private boolean buttonsEnabled = true;
    private Button filterComic;
    private ActionBarDrawerToggle drawerToggle;
    private enum AdjustType {
        BRIGHTNESS,
        THRESHOLD, SHADOW, TEMPERATURE, SATURATION, LUMINANCE_THRESHOLD, FILTERS, CONTRAST
    }

    private AdjustType currentAdjustType = AdjustType.BRIGHTNESS;  // Valor inicial predeterminado
    private TextView adjustTypeTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        filt= view.findViewById(R.id.filtersConstraint);
        seekBarConstraint= view.findViewById(R.id.constraintSeekbar);
        navigationView.getMenu().clear(); // Limpiar el menú actual
        navigationView.inflateMenu(R.menu.drawer_menu); // Inflar el menú deseado
        seekBarValueTextView = view.findViewById(R.id.progressSeekbarTextView);
        photoImageView = view.findViewById(R.id.photo_image_view);
        gpuImage = new GPUImage(getContext());
       // ImageView cropButton = view.findViewById(R.id.cropButton);

        Button menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            // Abre el drawer aquí
            // Por ejemplo, si estás utilizando NavigationView, puedes abrir el drawer de esta manera:
            drawerLayout.openDrawer(GravityCompat.END);
        });
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Acción a realizar cuando el DrawerLayout se abre
                // Por ejemplo, realizar una operación específica al abrir el menú
                gpuImage.setImage(fotoFinal);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Acción a realizar cuando el DrawerLayout se cierra
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        cropButton.setOnClickListener(v -> {
//            // Obtén el Drawable del archivo drawable
//            Drawable drawable = photoImageView.getDrawable();
//
//            // Convertir el Drawable en Bitmap
//            Bitmap editedBitmap = ((BitmapDrawable) drawable).getBitmap();
//
//            // Crea un archivo temporal para el archivo de origen
//            File sourceFile = createTempFile("source_image", ".png");
//
//            // Guarda el bitmap editado en el archivo de origen
//            saveBitmapToFile(editedBitmap, sourceFile);
//
//            // Crea un archivo temporal para el archivo de destino
//            File destinationFile = createTempFile("destination_image", ".png");
//
//            // Crea el Intent para el recorte de la imagen utilizando uCrop
//            UCrop.Options options = new UCrop.Options();
//            options.setAspectRatioOptions(1, new AspectRatio("1:1", 1, 1));
//            options.setToolbarColor(Color.RED);
//            options.setStatusBarColor(Color.RED);
//            options.setActiveControlsWidgetColor(Color.RED);
//            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//
//            UCrop.of(Uri.fromFile(sourceFile), Uri.fromFile(destinationFile))
//                    .withOptions(options)
//                    .start(getActivity(), this);  // Inicia la actividad de uCrop con el fragmento actual como resultado
//        });

        RelativeLayout buttonLayout = view.findViewById(R.id.relativeButtonMenuDrawer);

        buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        ImageView guardarButton = view.findViewById(R.id.guardarButton);
        guardarButton.setOnClickListener(v -> {
            guardarImagenEnGaleria();
        });
        filterSketch = view.findViewById(R.id.sketchFilter);
        filterSketch.setOnClickListener(v -> {
            if (buttonsEnabled) {
                buttonsEnabled = false;
                disableButtonsForDelay();
                applySketchFilter();
            }
        });
        filterComic = view.findViewById(R.id.comicFilter);
        filterComic.setOnClickListener(v -> {
            if (buttonsEnabled) {
                buttonsEnabled = false;
                disableButtonsForDelay();
                applyComicFilter();
            }
        });
        posterFilterButton = view.findViewById(R.id.posterFilter);
        posterFilterButton.setOnClickListener(v -> {
            if (buttonsEnabled) {
                buttonsEnabled = false;
                disableButtonsForDelay();
                applyPosterizeFilter();
            }
        });
        grayButton = view.findViewById(R.id.filter);
        grayButton.setOnClickListener(v -> {
            if (buttonsEnabled) {
                buttonsEnabled = false;
                disableButtonsForDelay();
                applyGreyScaleFilter();
            }
        });

        adjustSeekBar = view.findViewById(R.id.adjustSeekBar);
        adjustTypeTextView = view.findViewById(R.id.seekBarTextView);
        adjustSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private Timer timer = new Timer();
            private final long DELAY = 500; // Medio segundo

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Actualizar el último valor de la SeekBar
                lastSeekValue = progress;
                seekBarValueTextView.setText(String.valueOf(progress));

                // Cancelar el temporizador anterior (si existe)
                timer.cancel();

                // Programar un nuevo temporizador para realizar la actualización después del tiempo de retraso
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Verificar el tipo de ajuste actual y llamar al método correspondiente
                        switch (currentAdjustType) {
                            case BRIGHTNESS:
                                updateBrightness(lastSeekValue);
                                break;
                            case CONTRAST:
                                updateContrast(lastSeekValue);

                                break;
                            case SATURATION:
                                updateSaturation(lastSeekValue);
                                filt.setVisibility(View.INVISIBLE);

                                break;
                            case LUMINANCE_THRESHOLD:
                                updateLuminanceThreshold(lastSeekValue);

                                break;

                            default:
                                break;
                        }
                    }
                }, DELAY);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                comprimirImagen(fotoFinal);
//                bitmap=fotoFinal;
//                photoImageView.setImageBitmap(bitmap);
            }
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
                fotoFinal = selectedImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
                gpuImage.setImage(fotoFinal);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Configurar imagen inicial
//        // Obtener el Drawable del archivo drawable
//        Drawable drawable = photoImageView.getDrawable();
//
//        // Convertir el Drawable en Bitmap
//        bitmap = ((BitmapDrawable) drawable).getBitmap();
//        fotoFinal = bitmap;
//        // Establecer el Bitmap en el GPUImageView
//        gpuImage.setImage(bitmap);


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

    private void updateBrightness(final int progress) {
        if (currentAdjustType == AdjustType.BRIGHTNESS) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float brightnessValue = mapSeekBarValueToBrightness(progress);
                    gpuImage.setFilter(new GPUImageBrightnessFilter(brightnessValue));
                    fotoFinal = gpuImage.getBitmapWithFilterApplied();
                    photoImageView.setImageBitmap(fotoFinal);

                }
            });
        }
    }


    private float mapSeekBarValueToBrightness(int progress) {
        float minBrightness = -1.0f;
        float maxBrightness = 1.0f;
        float defaultBrightness = 0.0f;

        // Escala lineal desde el valor mínimo al valor máximo según el progreso de la SeekBar
        float scaledValue = minBrightness + ((float) progress / 100) * (maxBrightness - minBrightness);

        // Si el progreso es 50, el valor será el valor por defecto (0.0)
        if (progress == 50) {
            return defaultBrightness;
        }

        return scaledValue;
    }


    private void updateContrast(final int progress) {
        if (currentAdjustType == AdjustType.CONTRAST) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float contrastValue = mapSeekBarValueToContrast(progress);
                    gpuImage.setFilter(new GPUImageContrastFilter(contrastValue));
                    fotoFinal = gpuImage.getBitmapWithFilterApplied();
                    photoImageView.setImageBitmap(fotoFinal);

                }
            });
        }
    }

    private float mapSeekBarValueToContrast(int progress) {
        float minContrast = 0.0f;
        float maxContrast = 4.0f;
        float defaultContrast = 1.0f;

        // Escala lineal desde el valor mínimo al valor máximo según el progreso de la SeekBar
        float scaledValue = minContrast + ((float) progress / 100) * (maxContrast - minContrast);

        // Si el progreso es 50, el valor será el valor por defecto (1.0)
        if (progress == 50) {
            return defaultContrast;
        }

        return scaledValue;
    }


    private void updateShadow(int shadowIntensity) {
        if (currentAdjustType == AdjustType.SHADOW) {
            gpuImage.setFilter(new GPUImageHighlightShadowFilter(0, shadowIntensity));
            fotoFinal = gpuImage.getBitmapWithFilterApplied();
            photoImageView.setImageBitmap(fotoFinal);

        }
    }

    private void updateTemperature(int temperature) {
        if (currentAdjustType == AdjustType.TEMPERATURE) {
            gpuImage.setFilter(new GPUImageWhiteBalanceFilter(temperature, 0));
            fotoFinal = gpuImage.getBitmapWithFilterApplied();
            photoImageView.setImageBitmap(fotoFinal);

        }
    }

    private void updateSaturation(final int progress) {
        if (currentAdjustType == AdjustType.SATURATION) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float saturationValue = mapSeekBarValueToSaturation(progress);
                    gpuImage.setFilter(new GPUImageSaturationFilter(saturationValue));
                    fotoFinal = gpuImage.getBitmapWithFilterApplied();
                    photoImageView.setImageBitmap(fotoFinal);


                }
            });
        }
    }

    private float mapSeekBarValueToSaturation(int progress) {
        float minSaturation = 0.0f;
        float maxSaturation = 2.0f;
        float defaultSaturation = 1.0f;

        // Escala lineal desde el valor mínimo al valor máximo según el progreso de la SeekBar
        float scaledValue = minSaturation + ((float) progress / 100) * (maxSaturation - minSaturation);

        // Si el progreso es 50, el valor será el valor por defecto (1.0)
        if (progress == 50) {
            return defaultSaturation;
        }

        return scaledValue;
    }
    private void updateLuminanceThreshold(int progress) {
        if (currentAdjustType == AdjustType.LUMINANCE_THRESHOLD) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float thresHoldValue = mapSeekBarValueToThreshold(progress);
                    gpuImage.setFilter(new GPUImageLuminanceThresholdFilter(thresHoldValue));
                    photoImageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
                    fotoFinal = gpuImage.getBitmapWithFilterApplied();


                }
            });
        }
    }
    private float mapSeekBarValueToThreshold(int progress) {
        float minBrightness = 0.0f;
        float maxBrightness = 1.0f;
        float defaultBrightness = 0.0f;

        // Escala lineal desde el valor mínimo al valor máximo según el progreso de la SeekBar
        float scaledValue = minBrightness + ((float) progress / 100) * (maxBrightness - minBrightness);

        // Si el progreso es 50, el valor será el valor por defecto (0.0)
        if (progress == 50) {
            return defaultBrightness;
        }

        return scaledValue;
    }
    private void applyComicFilter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gpuImage.setFilter(new GPUImageToonFilter());
                fotoFinal = gpuImage.getBitmapWithFilterApplied();
                photoImageView.setImageBitmap(fotoFinal);
            }
        });
    }


    private void applySketchFilter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                gpuImage.setFilter(new GPUImageSketchFilter());
                fotoFinal = gpuImage.getBitmapWithFilterApplied();
                photoImageView.setImageBitmap(fotoFinal);
            }
        });
    }

 private void applyGreyScaleFilter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                gpuImage.setFilter(new GPUImageGrayscaleFilter());
                fotoFinal = gpuImage.getBitmapWithFilterApplied();
                photoImageView.setImageBitmap(fotoFinal);
            }
        });
    }

 private void applyPosterizeFilter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                gpuImage.setFilter(new GPUImagePosterizeFilter());
                fotoFinal = gpuImage.getBitmapWithFilterApplied();
                photoImageView.setImageBitmap(fotoFinal);
            }
        });
    }



    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // Dentro del método onNavigationItemSelected()
        int seekBarProgress = 50; // Valor para centrar la SeekBar
        adjustSeekBar.setProgress(seekBarProgress);
        if (id == R.id.brightness) {
            filt.setVisibility(View.INVISIBLE);
            seekBarConstraint.setVisibility(View.VISIBLE);
            currentAdjustType = AdjustType.BRIGHTNESS;
            System.out.println("Opción seleccionada: brightness");
            adjustSeekBar.setProgress(seekBarProgress);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adjustTypeTextView.setText("Brillo");
                }
            });


        } else if (id == R.id.contrast) {
            filt.setVisibility(View.INVISIBLE);
            seekBarConstraint.setVisibility(View.VISIBLE);
            currentAdjustType = AdjustType.CONTRAST;
            adjustSeekBar.setProgress(25);
            System.out.println("Opción seleccionada: contrast");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adjustTypeTextView.setText("Contraste");
                }
            });

        } else if (id == R.id.saturation) {
            filt.setVisibility(View.INVISIBLE);
            seekBarConstraint.setVisibility(View.VISIBLE);
            currentAdjustType = AdjustType.SATURATION;
            adjustSeekBar.setProgress(50);
            System.out.println("Opción seleccionada: saturation");
            adjustTypeTextView.setText("Saturation");

        } else if (id == R.id.blackwhite) {
            filt.setVisibility(View.INVISIBLE);
            seekBarConstraint.setVisibility(View.VISIBLE);
            currentAdjustType = AdjustType.LUMINANCE_THRESHOLD;
            adjustSeekBar.setProgress(50);
            System.out.println("Opción seleccionada: saturation");
            adjustTypeTextView.setText("ThresHold");


        } else if (id == R.id.imagesize) {
            seekBarConstraint.setVisibility(View.INVISIBLE);
            filt.setVisibility(View.INVISIBLE);
            System.out.println("Opción seleccionada: saturation");
            adjustTypeTextView.setText("Resize Image");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onCropButtonDrawerClicked();
                }
            });
        } else if (id == R.id.retouch) {
            currentAdjustType = AdjustType.FILTERS;
            filt.setVisibility(View.VISIBLE);
            seekBarConstraint.setVisibility(View.INVISIBLE);

        } else {
            System.out.println("Opción desconocida: " + item.toString());
        }
        // Cierra el drawer
        drawerLayout.closeDrawer(GravityCompat.END);
        // Regresa true para indicar que el evento de selección ha sido manejado
        return true;
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

    @Override
    public void onResume() {
        super.onResume();

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
    private void onCropButtonDrawerClicked() {
                    Drawable drawable = photoImageView.getDrawable();

            // Convertir el Drawable en Bitmap
            Bitmap editedBitmap = ((BitmapDrawable) drawable).getBitmap();

            // Crea un Intent para el recorte de la imagen
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            // Establece el tipo de datos y la imagen de origen para el recorte
            cropIntent.setDataAndType(getImageUri(getContext(), editedBitmap), "image/*");

            // Configura los extras para el recorte
            cropIntent.putExtra("crop", "true");

            // Establece el tamaño máximo deseado para la imagen recortada
            int maxOutputSize = 800;

            // Obtén la relación de aspecto original de la imagen
            float originalAspectRatio = (float) editedBitmap.getWidth() / editedBitmap.getHeight();

            int desiredWidth;
            int desiredHeight;

            if (editedBitmap.getWidth() > editedBitmap.getHeight()) {
                // La imagen es más ancha que alta
                desiredWidth = maxOutputSize;
                desiredHeight = (int) (maxOutputSize / originalAspectRatio);
            } else {
                // La imagen es más alta que ancha o tiene la misma altura que anchura
                desiredWidth = editedBitmap.getWidth();
                desiredHeight = editedBitmap.getHeight();
            }

            cropIntent.putExtra("outputX", desiredWidth);
            cropIntent.putExtra("outputY", desiredHeight);

            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", true);

            // Inicia la actividad de recorte con el resultado esperado
            startActivityForResult(cropIntent, REQUEST_CODE_CROP_PHOTO);

    }
    private void onCropButtonClicked() {
        Drawable drawable = photoImageView.getDrawable();
        Bitmap editedBitmap = ((BitmapDrawable) drawable).getBitmap();

        File sourceFile = createTempFile("source_image", ".png");
        saveBitmapToFile(editedBitmap, sourceFile);

        File destinationFile = createTempFile("destination_image", ".png");

        UCrop.Options options = new UCrop.Options();
        options.setAspectRatioOptions(1, new AspectRatio("1:1", 1, 1));
        options.setToolbarColor(Color.RED);
        options.setStatusBarColor(Color.RED);
        options.setActiveControlsWidgetColor(Color.RED);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        UCrop.of(Uri.fromFile(sourceFile), Uri.fromFile(destinationFile))
                .withOptions(options)
                .start(getActivity(), this);
    }
    private Bitmap applyFilterToBitmap(Bitmap bitmap, GPUImageFilter filter) {
        GPUImage gpuImage = new GPUImage(requireContext());
        gpuImage.setImage(bitmap);
        gpuImage.setFilter(filter);
        return gpuImage.getBitmapWithFilterApplied();
    }
    private void disableButtonsForDelay() {
        long delayMillis=1000;
        posterFilterButton.setEnabled(false);
        grayButton.setEnabled(false);
        filterSketch.setEnabled(false);
        filterComic.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                posterFilterButton.setEnabled(true);
                grayButton.setEnabled(true);
                filterSketch.setEnabled(true);
                filterComic.setEnabled(true);
                buttonsEnabled = true;
            }
        }, delayMillis);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pasar eventos del ActionBarDrawerToggle al onOptionsItemSelected
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Importante: Eliminar el DrawerListener al destruir la vista del fragmento
        drawerLayout.removeDrawerListener(drawerToggle);
    }
}
