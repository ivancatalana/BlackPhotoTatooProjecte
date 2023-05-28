package com.example.blackphototatoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.blackphototatoo.PremiumUser;
import com.example.blackphototatoo.PremiumUserViewHolder;
import com.google.firebase.database.annotations.Nullable;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;
import java.util.Locale;

public class PremiumUserAdapter extends RecyclerView.Adapter<PremiumUserViewHolder> {
    private List<PremiumUser> userList;
    public PremiumUserAdapter(List<PremiumUser> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public PremiumUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_premium_user, parent, false);
        return new PremiumUserViewHolder(view);
    }



        @Override
        public void onBindViewHolder (@NonNull PremiumUserViewHolder holder,int position){
            PremiumUser premiumUser = userList.get(position);
            Context context = holder.itemView.getContext(); // Obtener el contexto desde la vista padre
            holder.bind(context, premiumUser);

            holder.getRelativeButtonMap().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.popup_location, null);
                    builder.setView(dialogView);

                    // Obtener el nombre del elemento del RecyclerView
                    PremiumUser premiumUser = userList.get(holder.getAdapterPosition());
                    MapView mapView = dialogView.findViewById(R.id.mapView);
                    TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
                    String locationMapa = premiumUser.getLocation();
                    String locationName = premiumUser.getName().toUpperCase(Locale.ROOT);

                    if (locationMapa != null) {
                        String[] locationMapaSplit = locationMapa.split("-");
                        String latitudeString = locationMapaSplit[0];
                        String longitudeString = locationMapaSplit[1];
                        Configuration.getInstance().setUserAgentValue(context.getPackageName());
                        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                        mapView.setMultiTouchControls(true);
                        mapView.getController().setZoom(10);
                        double latitude = Double.parseDouble(latitudeString);
                        double longitude = Double.parseDouble(longitudeString);
                        GeoPoint startPoint = new GeoPoint(latitude, longitude);
                        int zoomLevel = 15;
                        mapView.getController().setCenter(startPoint);
                        mapView.getController().setZoom(zoomLevel);
                        titleTextView.setText(locationName);
                    } else {
                        ImageView backgroundImageView = dialogView.findViewById(R.id.mapView);

                        RequestOptions requestOptions = new RequestOptions()
                                .centerCrop() // Ajusta la imagen para que se ajuste al tamaño del ImageView
                                .placeholder(R.drawable.texture); // Imagen de textura de relleno

                        Glide.with(context)
                                .load(R.drawable.texture) // Carga la imagen de textura
                                .apply(requestOptions)
                                .into(backgroundImageView);
                    }

                    AlertDialog dialog = builder.create();
                    ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

    }
        @Override
    public int getItemCount() {
        return userList.size();
    }
}

