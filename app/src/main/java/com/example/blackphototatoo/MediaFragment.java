package com.example.blackphototatoo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MediaFragment extends Fragment {
    VideoView videoView;
    ImageView imageView,authorImage;
    TextView autor,name;
    public AppViewModel appViewModel;
    private NavController navController;
    private Post postDestino;
    private String uidPost;

    public MediaFragment() {
    // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) { // Inflate the layout for this fragmet
        return inflater.inflate(R.layout.fragment_media, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        navController = Navigation.findNavController(view);
        // accion personalizada al volver atras
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //  Handle the back button even
                // Aqui podemos configurar el comprotamiento del boton back
                navController.navigate(R.id.discoverFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
//
        imageView = view.findViewById(R.id.imageView);
        authorImage = view.findViewById(R.id.photoImageViewPost);
        autor = view.findViewById(R.id.authorTextName);
        name = view.findViewById(R.id.namePostText);

       // videoView = view.findViewById(R.id.videoView);
        appViewModel.postSeleccionado.observe(getViewLifecycleOwner(), post ->
        {
            autor.setText(post.author);
            name.setText(post.content);
            uidPost=post.uid;
            Glide.with(requireView()).load(post.authorPhotoUrl).into(authorImage);
            postDestino = post;
            if ("video".equals(post.mediaType) || "audio".equals(post.mediaType)) {
                MediaController mc = new MediaController(requireContext());
                mc.setAnchorView(videoView);
                videoView.setMediaController(mc);
                videoView.setVideoPath(post.mediaUrl);
                videoView.start();
            } else if ("image".equals(post.mediaType)) {
                Glide.with(requireView()).load(post.mediaUrl).into(imageView);
                Glide.with(requireView()).load(post.authorPhotoUrl).circleCrop().into(authorImage);
            }
        });
        ConstraintLayout profileClick= view.findViewById(R.id.mediaProfileConstraint);
      profileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid",uidPost);
                if (FirebaseAuth.getInstance().getUid().equals(uidPost)){
                    BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view);
                    bottomNavigationView.setSelectedItemId(R.id.bottom1Fragment);
                }
                else{
                    navController.navigate(R.id.profileFriendsFragment,bundle);
                }

            }
        });
    }
}