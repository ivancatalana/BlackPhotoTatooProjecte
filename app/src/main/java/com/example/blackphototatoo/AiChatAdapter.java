package com.example.blackphototatoo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AiChatAdapter extends RecyclerView.Adapter<AiChatAdapter.ChatViewHolder> {
    private List<AiChatMessage> messages;
    private Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private OnItemClickListener listener;
    private FragmentReloadListener reloadListener;

    public void setDatos(List<AiChatMessage> datos) {
        messages = datos;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }
    public AiChatAdapter(List<AiChatMessage> messages, Context context) {
        this.messages = messages;
        this.context=context;
    }

    // Constructor del adaptador
    public AiChatAdapter(FragmentReloadListener listener) {
        this.reloadListener = listener;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Crear una instancia de ViewHolder
        View view = inflater.inflate(R.layout.item_ai_chat_message, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        AiChatMessage message = messages.get(position);

        holder.promptText.setText(message.getPrompt());

         // Carga la imagen desde una URI
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Se tienen los permisos, cargar la imagen
            Glide.with(holder.itemView)
                    .load(message.getImageUri())
                    .into(holder.imageUriImageView);
        } else {
            // No se tienen los permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        // Cargamos la imagen del server
        Glide.with(holder.itemView)
                .load(message.getImageUrl())
                .into(holder.imageUrlImageView);

        // Muestra la fecha utilizando el formato deseado
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(message.getDate());
        holder.dateTextView.setText(formattedDate);
        holder.promptTextDown.setText(message.getPrompt());
        // Configurar el listener de clic para la imagen
        // Configurar el listener de pulsación larga para la imagen
        holder.imageUriImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Mostrar el PopupMenu al realizar una pulsación larga en la imagen
                showPopupMenu(holder.imageUriImageView, message);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessages(List<AiChatMessage> newMessages) {
        // Invierte el orden de la lista de mensajes
        Collections.reverse(newMessages);
        // Agrega los mensajes invertidos a tu lista de mensajes actual
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView promptText;
        TextView promptTextDown;
        ImageView imageUriImageView;
        ImageView imageUrlImageView;
        TextView dateTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            promptText = itemView.findViewById(R.id.promptTextView);
            promptTextDown = itemView.findViewById(R.id.promptView);
            imageUriImageView = itemView.findViewById(R.id.imageUriImageView);
            imageUrlImageView = itemView.findViewById(R.id.imageUrlImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
    public void addMessagesAtStart(List<AiChatMessage> messages) {
        this.messages.addAll(0, messages);
        notifyDataSetChanged();
    }
    private void guardarImagenEnGaleria(Context context,String imageUrl) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Aquí tenemos el Bitmap de la imagen cargada desde la URL

                        // Genera un nombre de archivo aleatorio para la imagen
                        String randomFileName = UUID.randomUUID().toString();
                        String imageFileName = randomFileName + ".jpg";

                        // Guardamos el Bitmap en la galería
                        String savedImagePath = MediaStore.Images.Media.insertImage(
                                context.getContentResolver(),
                                resource,
                                imageFileName,
                                "Image saved from URL"
                        );

                        if (savedImagePath != null) {
                            Toast.makeText(context, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showPopupMenu(View anchorView, AiChatMessage message) {
        PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        // Configuramos el listener de clic para las opciones del menú
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_save_gallery) {
                    guardarImagenEnGaleria(anchorView.getContext(), message.getImageUrl());
                    return true;
                } else if (itemId == R.id.menu_delete_post) {
                    CollectionReference serverEditionsRef = FirebaseFirestore.getInstance().collection("serverEditions");
                    String serverEditionId = FirebaseAuth.getInstance().getUid();

                    CollectionReference publicacionesRef = serverEditionsRef.document(serverEditionId).collection("publicaciones");
                    Query query = publicacionesRef.whereEqualTo("rutaImagenRespuesta", message.getImageUrl());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Eliminar el documento que cumple con el filtro
                                    document.getReference().delete();
                                    // Dentro del método onClick() de tu ViewHolder
                                    // Notifica los cambios en el adaptador
                                    notifyDataSetChanged();

                                    // Llama al método onReloadFragment() en el reloadListener
                                    if (reloadListener != null) reloadListener.onReloadFragment();
                                    else System.out.println(".............................................................-------------null");
                                                                   }
                            } else {
                                Log.d("FirebaseQuery", "Error getting documents: ", task.getException());
                            }
                        }

                    });

                    return true;
                }
                return false;
            }
        });

        // Mostrar el menú emergente
        popupMenu.show();

        // Establecer fondo blanco para el menú emergente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Para Android Q y versiones posteriores
           //
            // popupMenu.setPopupBackgroundDrawable(new ColorDrawable(Color.WHITE));
        } else {
            // Para versiones anteriores a Android Q
            try {
                Field field = popupMenu.getClass().getDeclaredField("mPopup");
                field.setAccessible(true);
                Object menuPopupHelper = field.get(popupMenu);
                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                Method setPopupBackgroundDrawable = classPopupHelper.getMethod("setPopupBackgroundDrawable", Drawable.class);
                setPopupBackgroundDrawable.invoke(menuPopupHelper, new ColorDrawable(Color.WHITE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface FragmentReloadListener {
        void onReloadFragment();
    }
    public interface OnItemClickListener {
        void onItemDeleted(int position);
    }
}
