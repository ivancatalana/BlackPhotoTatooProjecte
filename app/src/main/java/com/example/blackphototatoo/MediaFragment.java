package com.example.blackphototatoo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

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
                             Bundle savedInstanceState) { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        navController = Navigation.findNavController(view);
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
      authorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //appViewModel.postSeleccionado.setValue(postDestino);
                System.out.println("------------------------------------------------------uidPost:  "+ uidPost);
                Bundle bundle = new Bundle();

                //bundle.putString("nombre", post.author);
                bundle.putString("uid",uidPost);
                //bundle.putInt("foto", R.drawable.profile);


                navController.navigate(R.id.profileFriendsFragment,bundle);
                // Acción a realizar cuando se hace clic en el ImageView
                // Por ejemplo, mostrar un mensaje o navegar a otra pantalla
            }
        });
    }
}