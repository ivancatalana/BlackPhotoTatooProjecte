package com.example.blackphototatoo;

import android.net.Uri;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NuevaPublicacionFragment extends Fragment{

    NavController navController;   // <-----------------

    public AppViewModel appViewModel;

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
            return inflater.inflate(R.layout.fragment_nueva_publicacion, container, false);
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


            //Añadimos La referencia del appviewModel que declaramos arriba Y losListeners para los botones de subir multimedia

            appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

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
            System.out.println(user.getPhotoUrl().toString());
            // Obtén la fecha actual
            Date currentDate = new Date();

            // Formatea la fecha según los patrones definidos
            postDateAndTime = formatoPost.format(currentDate);
            dateTimeOrdenada = fechaOrdenada.format(currentDate);
            String contentName = "Prueba contentName";
            System.out.println(postContent);
            if(user.getDisplayName()!=null){
                postName=user.getDisplayName();
            }
            else {
                postName = user.getEmail();
            }
            Publicacion post = new Publicacion(user.getUid(), postName,  postDateAndTime, dateTimeOrdenada, user.getPhotoUrl().toString(),postContent, mediaUrl, mediaTipo);

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
            FirebaseFirestore.getInstance().collection("publications")
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

        private void seleccionarImagen() {
            mediaTipo = "image"; galeria.launch("image/*");
        }


    }

