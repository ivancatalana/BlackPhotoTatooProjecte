package com.example.blackphototatoo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;

public class PostViewFragment extends Fragment {
    ImageView postPhotoImageView, mediaImageView;
    TextView displayNameTextView, contentTextView, dateTimeText;
    NavController navController;


    public PostViewFragment() {
        // Required empty public constructor
    }


    public static PostViewFragment newInstance(String param1, String param2) {
        PostViewFragment fragment = new PostViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //////    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla

        View view = inflater.inflate(R.layout.fragment_post_view, container, false);


        // Set the layout manager

        Bundle args = getArguments();
        if (args != null) {
            postPhotoImageView = view.findViewById(R.id.photoImageView3);
            displayNameTextView = view.findViewById(R.id.userPost);
            contentTextView = view.findViewById(R.id.contentPost);
            dateTimeText = view.findViewById(R.id.timePost);
            mediaImageView = view.findViewById(R.id.photoImageView2);
            //emailTextView = view.findViewById(R.id.emailTextView);
            displayNameTextView.setText(args.getString("nombre"));
            dateTimeText.setText(args.getString("time"));
            contentTextView.setText(args.getString("contenido"));


            if (args.getString("foto") != null) {
                Glide.with(getContext()).load(args.getString("foto")).circleCrop().into(postPhotoImageView);
            }

            // Miniatura de media
            if (args.getString("fotoMedia") != null)
                Glide.with(getContext()).load(args.getString("fotoMedia")).centerCrop().into(mediaImageView);

              /*  if ("audio".equals(post.mediaType)) {
                    Glide.with(requireView()).load(R.drawable.audio).centerCrop().into(holder.mediaImageView);
                } else {
                    Glide.with(requireView()).load(post.mediaUrl).centerCrop().into(holder.mediaImageView);
                }
                holder.mediaImageView.setOnClickListener(view -> { appViewModel.postSeleccionado.setValue(post); navController.navigate(R.id.mediaFragment);
                });
            } else { holder.mediaImageView.setVisibility(View.GONE);
            }
            //
            //     postPhotoImageView.setImageResource(args.getInt("foto"));

        }

        // ...

               */
        }
        return view;
    }

}
