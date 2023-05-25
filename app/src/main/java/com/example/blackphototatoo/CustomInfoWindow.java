package com.example.blackphototatoo;

import android.widget.ImageView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {

    private ImageView mImageView;

    public CustomInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);

        // encuentra la vista de ImageView en tu diseño personalizado
        mImageView = (ImageView) mView.findViewById(R.id.postsProfileImageView);
    }

    @Override
    public void onOpen(Object item) {
        // obtiene la URL de la imagen de tu objeto de marcador
        String imageUrl = ((Marker) item).getSnippet();

        // carga la imagen con Glide en la vista ImageView
     //   Glide.with(this)
        //        .load(imageUrl)
      //          .into(mImageView);
    }

    @Override
    public void onClose() {
        // elimina la imagen de la vista ImageView
      //  Glide.with(mContext)
      //          .clear(mImageView);
    }
}
