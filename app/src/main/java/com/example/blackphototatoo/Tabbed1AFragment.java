package com.example.blackphototatoo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Tabbed1AFragment extends Fragment {
    private RecyclerView recyclerView;
    public AppViewModel appViewModel;
    RecyclerView postsRecyclerView;
    private Parcelable recyclerViewState;
    NavController navController;
    String uidPostProfile;
    private  String photoUrl;
    private  String name;
    private ConstraintLayout confirmDeleteConstraint;
    private LinearLayout menuEditLinearLayout;
    public Tabbed1AFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tabbed1_a, container, false);
        //////    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
        // Find the RecyclerView in the layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerProfile);
        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        editRatePostsButton = view.findViewById(R.id.editRatePostsButton);
//        editRatePostsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopupOptions();
//            }
//        });

        postsRecyclerView = view.findViewById(R.id.recyclerProfile);
        uidPostProfile= FirebaseAuth.getInstance().getUid();        // Consulta con indice compuesto que obliga a estar  habilitada en firebase
        Query query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("uid", uidPostProfile).orderBy("ordenadaDateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();
        PostsAdapter adapter = new PostsAdapter(options);
        postsRecyclerView.setAdapter(adapter);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.PostViewHolder> {
        // Metodo para contar los posts
        @Override
        public int getItemCount() {
            return super.getItemCount(); // Retorna el número de items del adaptador
        }
    public PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }

    @NonNull
    @Override
    public PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.objeto_imagen_layout, parent, false));
    }


    @Override

    protected void onBindViewHolder(@NonNull PostsAdapter.PostViewHolder holder, int position, @NonNull final Post post) {

        Glide.with(getContext()).load(post.mediaUrl).into(holder.mediaImageView);
        holder.dateTimeTextView.setText(post.dateTimePost);
        holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
        holder.namePostsProfile.setText(post.content);
    }
    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaImageView;
        TextView dateTimeTextView, numLikesTextView, namePostsProfile;
        Button editRatePostsButton; // Agrega esta línea

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            //   postPhotoImageView=itemView.findViewById(R.id.photoImageView);
            // Instanciar el ConstraintLayout y el LinearLayout

            editRatePostsButton = itemView.findViewById(R.id.editRatePostsButton);
            editRatePostsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupOptions();
                }
            });
            mediaImageView = itemView.findViewById(R.id.postsProfileImageView);
            dateTimeTextView = itemView.findViewById(R.id.postsProfileDate);
            numLikesTextView = itemView.findViewById(R.id.rankingfoto);
            namePostsProfile = itemView.findViewById(R.id.postsProfileName);

        }
        private void showConfirmationDialog(String postId, AlertDialog dialog, Button confirmButton , Button cancelButton) {

            // Configurar los botones de "Sí" y "No" en la ventana de confirmación


            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para "Sí" - Confirmar eliminación
                    deletePost(postId);
                    // Ocultar la ventana de confirmación y mostrar el ConstraintLayout original
                    dialog.dismiss();

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para "No" - Cancelar
                    // Ocultar la ventana de confirmación y mostrar el ConstraintLayout original
                    dialog.dismiss();

                }
            });
        }


        private void showPopupOptions() {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View customPopupView = inflater.inflate(R.layout.custom_popup_dialog, null);
            builder.setView(customPopupView);
            menuEditLinearLayout = customPopupView.findViewById(R.id.menuEditLinearLayout);
            confirmDeleteConstraint = customPopupView.findViewById(R.id.confirmDeleteConstraint);

            // Configurar opciones y eventos de clic
            Button option1Button = customPopupView.findViewById(R.id.option1Button);
            Button option2Button = customPopupView.findViewById(R.id.option2Button);
            Button option3Button = customPopupView.findViewById(R.id.option3Button);
            Button confirmButton = customPopupView.findViewById(R.id.confirmDelete);
            Button cancelButton = customPopupView.findViewById(R.id.cancelDelete);
            final AlertDialog dialog = builder.create();

            option1Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            option2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para la opción 2 - Eliminar
                    // Ocultar el ConstraintLayout actual
                    customPopupView.findViewById(R.id.option1Button);
                    menuEditLinearLayout.setVisibility(View.GONE);
                    // Mostrar otro ConstraintLayout con la ventana de confirmación
                    confirmDeleteConstraint.setVisibility(View.VISIBLE);
                }
            });

            option3Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para la opción 3
                    dialog.dismiss(); // Cerrar el diálogo emergente
                    System.out.println("---------------------------------------------boton 3");

                }
            });
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para "Sí" - Confirmar eliminación
                    String postId = getSnapshots().getSnapshot(getAdapterPosition()).getId();
                    deletePost(postId);
                    // Ocultar la ventana de confirmación y mostrar el ConstraintLayout original
                    dialog.dismiss();

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para "No" - Cancelar
                    // Ocultar la ventana de confirmación y mostrar el ConstraintLayout original
                    dialog.dismiss();

                }
            });

            dialog.show();
        }
    }
    }

    //Codigo para que no se bloquee el recyclerview al cerrar app o bloquear pantalla
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewState = postsRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recycler_state", recyclerViewState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postsRecyclerView != null && postsRecyclerView.getLayoutManager() != null) {
            postsRecyclerView.getLayoutManager().removeAllViews(); // Limpia el RecyclerView
        }
    }

    private void deletePost(String postId) {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Eliminación exitosa, realiza cualquier acción adicional si es necesario
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar el post, maneja el error si es necesario
                    }
                });
    }
}