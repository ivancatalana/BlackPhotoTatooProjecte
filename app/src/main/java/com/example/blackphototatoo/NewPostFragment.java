package com.example.blackphototatoo;

import android.app.Activity;
import android.content.Intent;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NewPostFragment extends Fragment {
    NavController navController;   // <-----------------

    public AppViewModel appViewModel;

    Button publishButton;
    EditText postConentEditText;
    String mediaTipo;
    Uri mediaUri;
    String postDateAndTime;
    String dateTimeOrdenada;
    String postName;

  //  DateTimeFormatter formatoPost =  DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");
   // DateTimeFormatter fechaOrdenada =  DateTimeFormatter.ofPattern("yyMMddHHmmss");
// Define los patrones de formato de fecha
    SimpleDateFormat formatoPost = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
    SimpleDateFormat fechaOrdenada = new SimpleDateFormat("yyMMddHHmmss");


    public NewPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }


    // ...

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


        //Añadimos La referencia del appviewModel que declaramos arriba Y losListeners para los botones de subir multimedia

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        view.findViewById(R.id.camara_fotos).setOnClickListener(v -> tomarFoto());
        view.findViewById(R.id.camara_video).setOnClickListener(v -> tomarVideo());
        view.findViewById(R.id.grabar_audio).setOnClickListener(v -> grabarAudio());
        view.findViewById(R.id.imagen_galeria).setOnClickListener(v -> seleccionarImagen());
        view.findViewById(R.id.video_galeria).setOnClickListener(v -> seleccionarVideo());
        view.findViewById(R.id.audio_galeria).setOnClickListener(v -> seleccionarAudio());
        appViewModel.mediaSeleccionado.observe(getViewLifecycleOwner(), media -> {
            this.mediaUri = media.uri;
            this.mediaTipo = media.tipo;
            Glide.with(this).load(media.uri).into((ImageView) view.findViewById(R.id.previsualizacion));
        });
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

    }
/*
    private void publicar() {
        String postContent = postConentEditText.getText().toString();
        if(TextUtils.isEmpty(postContent)){
            postConentEditText.setError("Required");
            return;
        }

        publishButton.setEnabled(false);

        guardarEnFirestore(postContent);
    }
    private void guardarEnFirestore(String postContent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String photo;
        String postName;
        if(user.getDisplayName()!=null){
           postName=user.getDisplayName();
        }
        else {
            postName=user.getEmail();
        }
        if(user.getPhotoUrl()!=null){
            photo=user.getPhotoUrl().toString();
        }
        else {
            //photo=null;

            photo ="https://cdn-icons-png.flaticon.com/512/25/25231.png";
        }
        // postDateAndTime = Date.

        postDateAndTime = LocalDateTime.now().format(formatoPost);
        dateTimeOrdenada = LocalDateTime.now().format(fechaOrdenada);
        Post post = new Post(user.getUid(),postName ,postDateAndTime,dateTimeOrdenada,photo, postContent,null,null);
        */
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
            System.out.println(user.getPhotoUrl().toString());
            // Obtén la fecha actual
            Date currentDate = new Date();

            // Formatea la fecha según los patrones definidos
            postDateAndTime = formatoPost.format(currentDate);
            dateTimeOrdenada = fechaOrdenada.format(currentDate);
                                 //String uid, String author, String dateTimePost,String ordenadaDateTime,  String authorPhotoUrl, String content,String mediaUrl, String mediaType
           // postDateAndTime =  new Date().toString();// LocalDateTime.now().format(formatoPost);
           // dateTimeOrdenada = new Date().toString();//LocalDateTime.now().format(fechaOrdenada);
            String contentName = "Prueba contentName";
            System.out.println(postContent);
            if(user.getDisplayName()!=null){
                postName=user.getDisplayName();
            }
            else {
                postName = user.getEmail();
            }
            Post post = new Post(user.getUid(), postName,  postDateAndTime, dateTimeOrdenada, user.getPhotoUrl().toString(),postContent, mediaUrl, mediaTipo);

          /*  UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(mediaUrl))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });

           */
            System.out.println(post);
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
    private void pujaIguardarEnFirestore(final String postText) { FirebaseStorage.getInstance().getReference(mediaTipo + "/" +
                    UUID.randomUUID()) .putFile(mediaUri)
            .continueWithTask(task -> task.getResult().getStorage().getDownloadUrl())
            .addOnSuccessListener(url -> guardarEnFirestore(postText, url.toString()));
    }
    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        appViewModel.setMediaSeleccionado(uri, mediaTipo); });
    private final ActivityResultLauncher<Uri> camaraFotos = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
        appViewModel.setMediaSeleccionado(mediaUri, "image");
    });
    private final ActivityResultLauncher<Uri> camaraVideos = registerForActivityResult(new ActivityResultContracts.TakeVideo(), isSuccess -> {
        appViewModel.setMediaSeleccionado(mediaUri, "video"); });
    private final ActivityResultLauncher<Intent> grabadoraAudio = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) { appViewModel.setMediaSeleccionado(result.getData().getData(),
                "audio"); }
    });
    private void seleccionarImagen() {
    mediaTipo = "image"; galeria.launch("image/*");
}
    private void seleccionarVideo() { mediaTipo = "video"; galeria.launch("video/*");
    }
    private void seleccionarAudio() { mediaTipo = "audio"; galeria.launch("audio/*");
    }
    private void tomarFoto() { try {
        mediaUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", File.createTempFile("img", ".jpg", requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        camaraFotos.launch(mediaUri); } catch (IOException e) {}
    }
    private void tomarVideo() { try {
        mediaUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", File.createTempFile("vid", ".mp4", requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)));
        camaraVideos.launch(mediaUri); } catch (IOException e) {}
    }
    private void grabarAudio() { grabadoraAudio.launch(new
            Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)); }

}
