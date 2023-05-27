package com.example.blackphototatoo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AIEditorFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private ImageView imageView;
    private ImageView serverImageView;
    private Button uploadButton;
    private Button buttonRotate;
    private EditText promptEdit;
    private boolean isButtonEnabled = true;
    private Bitmap bitmap;
    private String serverURL = "http://ivancatalana.duckdns.org:8000/";  // Reemplaza con la URL de tu servidor Flask
    private String rutaImagenGuardada;    // Variable para almacenar el filepath temporal de la imagen devuelta por el servidor
    private String serverImageFilePath;
    private String prompt;
    private String rutaOrigenImagen;
    private String  imageUrl;
    private Uri imageUriGuardada; // Declarar la variable a nivel de clase
    // Declaración del Handler
    private Handler handler = new Handler();
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_editor, container, false);

        buttonRotate = view.findViewById(R.id.btnRotate);
        buttonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Girar la imagen 90 grados hacia la derecha
                rotateImage90Degrees();
            }
        });
        imageView = view.findViewById(R.id.imageView);
        serverImageView = view.findViewById(R.id.serverImageView);
        uploadButton = view.findViewById(R.id.uploadButton);
        promptEdit = view.findViewById(R.id.promtEditText);
        uploadButton.setEnabled(false);
        ImageButton btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView.getTag() == null) {
                    Toast.makeText(getActivity(), "Error: No hay imagen seleccionada", Toast.LENGTH_SHORT).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(promptEdit.getWindowToken(), 0);
                } else {
                    Uri imageUri = Uri.parse(imageView.getTag().toString());
                    uploadFile(imageUri, promptEdit.getText().toString());

                    // Cargar la imagen desde la URL utilizando Glide y mostrarla en el ImageView
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(requireContext())
                                  // .load("https://static.vecteezy.com/system/resources/thumbnails/008/034/405/small/loading-bar-doodle-element-hand-drawn-vector.jpg")
                                    .asGif()
                                    .load("https://cdn.kibrispdr.org/data/1789/loading-bar-gif-36.gif")
                                    .into(serverImageView);
                        }
                    });

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(promptEdit.getWindowToken(), 0);
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    pickImageFromGallery();
                }
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showPopupBlackWhite(imageUriGuardada);

            }
        });
        serverImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Se configura un temporizador para esperar 1 segundo antes de mostrar el diálogo
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Se muestra el diálogo
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setMessage("¿Qué deseas hacer con la imagen?")
                                .setPositiveButton("Guardar en galería", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // El usuario ha seleccionado guardar la imagen en la galería
                                        guardarImagenEnGaleria(true);
                                    }
                                })
                                .setNegativeButton("Utilizar en ImageView", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        guardarImagenEnGaleria(false);

                                        // El usuario ha seleccionado utilizar la imagen en el ImageView

                                        // Cargar la imagen como miniatura respetando su aspecto original
                                        int targetWidth = imageView.getWidth();
                                        int targetHeight = imageView.getHeight();
                                        Bitmap bitmap = decodeSampledBitmapFromFile(imageUriGuardada.toString(), targetWidth, targetHeight);

                                        // Mostrar la imagen corregida en el ImageView
                                        imageView.setImageBitmap(bitmap);
                                        imageView.setTag(imageUriGuardada.toString());
                                        uploadButton.setEnabled(true);


                                        // Aquí puedes realizar cualquier otra lógica necesaria
                                    }
                                });
                        builder.create().show();
                    }
                }, 1000); // Esperar 1 segundo (1000 milisegundos)

                // Indicar que el evento se ha gestionado
                return true;
            }
        });

        // Cierra el teclado al darle a intro

        promptEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(promptEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageUriGuardada=imageUri;

            // Obtener la ruta del archivo de la URI
            String filePath = getImagePathFromUri(imageUri);

            // Cargar la imagen como miniatura respetando su aspecto original
            int targetWidth = imageView.getWidth();
            int targetHeight = imageView.getHeight();
            Bitmap bitmap = decodeSampledBitmapFromFile(filePath, targetWidth, targetHeight);

            // Mostrar la imagen corregida en el ImageView
            imageView.setImageBitmap(bitmap);
            imageView.setTag(imageUri.toString());
            uploadButton.setEnabled(true);
        }
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        // Primero, se obtienen las dimensiones del archivo de imagen sin decodificar
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calcular el factor de escala para ajustar las dimensiones de la imagen a las dimensiones de la miniatura deseada
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decodificar el archivo de imagen con el factor de escala calculado
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Dimensiones originales de la imagen
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // Si las dimensiones originales de la imagen son mayores que las dimensiones de la miniatura deseada,
        // se calcula el factor de escala apropiado
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Se calcula el valor más grande inSampleSize que es una potencia de 2 y mantiene ambas dimensiones
            // de la imagen mayores que las dimensiones de la miniatura deseada
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        }
    }
    private String getImagePathFromUri(Uri uri) {
        String imagePath = null;
        if (getContext() != null) {
            Cursor cursor = null;
            try {
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    imagePath = cursor.getString(columnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return imagePath;
    }
    private void uploadFile(Uri uri , String prompt) {
        // Ruta del archivo en el dispositivo Android

        String filePath = getImagePathFromUri(uri);
        String promptText = prompt;
        if (promptText.isEmpty()) {
            Toast.makeText(getContext(), "El campo de texto está vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        //Guardar prompt y uri de la imagen enla coleccion

        // Rotar la imagen según los metadatos de rotación
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap = rotateImageBasedOnExif(bitmap, filePath);

        // Crea un cliente OkHttp con un tiempo de espera extendido
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS) // Establece el tiempo de espera para leer la respuesta del servidor
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();


        String randomFileName = UUID.randomUUID().toString() + ".jpg";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapData = baos.toByteArray();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", randomFileName,
                        RequestBody.create(MediaType.parse("image/jpeg"), bitmapData))
                .addFormDataPart("prompt", promptText)  //
                .build();

        //Ponemos vacio el campo edit Text
        promptEdit.setText("");

        // Crear solicitud POST
        Request request = new Request.Builder()
                .url("http://ivancatalana.duckdns.org:8000/upload")
                .post(requestBody)
                .build();

        // Ejecutar la solicitud de forma asíncrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Procesar la respuesta del servidor
                    System.out.println("Respuesta del servidor: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        imageUrl = jsonObject.getString("link");

                        guardarRegistroEdiciones(promptText,uri,imageUrl);



                        // Cargar la imagen desde la URL utilizando Glide y mostrarla en el ImageView
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .into(serverImageView);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Manejar la respuesta de error del servidor
                    System.out.println("Error en la respuesta del servidor: " + response.code());
                }
            }
        });
    }


    private void rotateImage90Degrees() {
        // Obtén la imagen actual (puedes ajustar esto según tu implementación)
        ImageView imageView = requireView().findViewById(R.id.imageView);
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Crea una matriz de rotación de 90 grados
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        // Aplica la matriz de rotación a la imagen
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Establece la imagen rotada en el ImageView
        imageView.setImageBitmap(rotatedBitmap);
    }
    private Bitmap rotateImage(Bitmap bitmap2, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), matrix, true);
    }
    private Bitmap rotateImageBasedOnExif(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationAngle = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = 270;
                    break;
            }

            if (rotationAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationAngle);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    private long getFileSizeFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                long size = inputStream.available();
                inputStream.close();
                return size;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void guardarImagenEnGaleria(boolean generarNombreAleatorio) {
        BitmapDrawable drawable = (BitmapDrawable) serverImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // Aquí guardas la imagen en la galería usando el bitmap obtenido
        // Puedes utilizar el MediaStore para guardar la imagen
        String imageFileName;
        String randomFileName = UUID.randomUUID().toString();
        imageFileName = randomFileName + ".jpg";


        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
        values.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());

        ContentResolver resolver = requireActivity().getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        imageUriGuardada = imageUri;
        OutputStream outputStream;
        try {
            outputStream = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            // Guardar la ruta del archivo guardado en la variable rutaImagenGuardada
            rutaImagenGuardada = getImagePathFromUri(imageUriGuardada);

            Toast.makeText(requireContext(), "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!generarNombreAleatorio){

            // Obtener la ruta del archivo de la URI
            String filePath = getImagePathFromUri(imageUriGuardada);

//            // Cargar la imagen como miniatura respetando su aspecto original
//            int targetWidth = imageView.getWidth();
//            int targetHeight = imageView.getHeight();
//            Bitmap bitmap2 = decodeSampledBitmapFromFile(filePath, targetWidth, targetHeight);
//
//            // Mostrar la imagen corregida en el ImageView
//            imageView.setImageBitmap(bitmap2);
//            imageView.setTag(bitmap2.toString());
//            uploadButton.setEnabled(true);
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imageView);

        }
    }


    private String getTempFilePath() {
        // Obtener el directorio de almacenamiento externo privado
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crear el filepath temporal utilizando el directorio de almacenamiento y el nombre de archivo constante
        String tempFilePath = storageDir.getAbsolutePath() + File.separator + "temp.jpg";
        // Obtener la ruta del archivo guardado
        return tempFilePath;
    }

    private String saveImageFromUrl(String imageUrl) {
        // Obtener el directorio de almacenamiento externo privado utilizando el contexto del fragment
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crear el filepath temporal utilizando el directorio de almacenamiento y el nombre de archivo constante
        String tempFilePath = storageDir.getAbsolutePath() + File.separator + "temp.jpg";

        // Crear el filepath y el archivo de destino
        String fileName = "temp.jpg";
        File destFile = new File(storageDir, fileName);

        // Descargar y guardar la imagen desde la URL en el archivo de destino
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFilePath;
    }

    public void guardarRegistroEdiciones(String prompt, Uri rutaImagen, String respuestaServidor) {
        // Obtener el UID del usuario actual
        String uidUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Configurar la conexión con Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Obtener una referencia a la colección "serverEditions"
        CollectionReference collection = firestore.collection("serverEditions");

        // Obtener una referencia al documento del usuario actual en la colección "serverEditions"
        DocumentReference document = firestore.collection("serverEditions").document(uidUsuarioActual);

        // Crear un nuevo documento en la subcolección "publicaciones" con un ID automático
        CollectionReference subcoleccion = document.collection("publicaciones");
        DocumentReference nuevaPublicacion = subcoleccion.document();

        // Crear un mapa de datos con los campos que deseas almacenar
        Map<String, Object> datos = new HashMap<>();
        datos.put("prompt", prompt);
        datos.put("rutaImagen", rutaImagen);
        datos.put("rutaImagenRespuesta", respuestaServidor);
        datos.put("time", FieldValue.serverTimestamp());

        // Guardar el mapa de datos en el documento
        nuevaPublicacion.set(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La información se guardó correctamente en Firestore
                        // Continuar con el proceso de enviar la imagen al servidor
                     //   enviarImagenAlServidor(rutaImagen);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al guardar la información en Firestore
                        // Manejar el error según tus necesidades
                    }
                });
    }
    public void showPopupBlackWhite(Uri uri) {
        // Inflar el diseño del popup
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Crear el popup
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Obtener las referencias a las SeekBar del popup
        SeekBar seekBar1 = popupView.findViewById(R.id.seekBar1);
        SeekBar seekBar2 = popupView.findViewById(R.id.seekBar2);

        // Establecer el rango de valores de las SeekBar
        seekBar1.setMax(255);
        seekBar2.setMax(255);

        // Obtener las referencias a los botones del popup
        Button acceptButton = popupView.findViewById(R.id.acceptButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        // Agregar funcionalidad a los botones
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al aceptar el popup
                // Obtener los valores seleccionados de las SeekBar
                int value1 = seekBar1.getProgress();
                int value2 = seekBar2.getProgress();
                // Realizar la acción deseada con los valores seleccionados
                uploadFile(uri, "opencv "+ value1 + " " + value2);

                // Cerrar el popup
                popupWindow.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al cancelar el popup
                // Cerrar el popup
                popupWindow.dismiss();
            }
        });

        // Mostrar el popup en la posición deseada
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

}