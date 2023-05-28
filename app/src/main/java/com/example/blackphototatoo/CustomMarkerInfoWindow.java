package com.example.blackphototatoo;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class CustomMarkerInfoWindow extends MarkerInfoWindow {
    private ImageView imageView;
    private TextView textView;

    public CustomMarkerInfoWindow(MapView mapView, String imageUrl, String text) {
        super(R.layout.custom_marker_info_window, mapView);
        imageView = (ImageView) mView.findViewById(R.id.postsProfileImageView);
        textView = (TextView) mView.findViewById(R.id.markerTextView);
        textView.setText(text);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_image) // Imagen de carga mientras se carga la imagen real
                .error(R.drawable.unavailable); // Imagen de error si no se puede cargar la imagen

        Glide.with(mapView.getContext())
                .load(imageUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void onOpen(Object item) {
        if (item instanceof Marker) {
            Marker marker = (Marker) item;
            MapView mapView = getMapView();

            // Resto del código para el infoWindow
        }
    }
}

