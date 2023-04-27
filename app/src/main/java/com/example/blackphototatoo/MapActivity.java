package com.example.blackphototatoo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.blackphototatoo.databinding.ActivityMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;


public class MapActivity extends AppCompatActivity {
    MapView map;
    ActivityMapBinding binding;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = (MapView) findViewById(R.id.map);
        map.getTileProvider().clearTileCache();
        Configuration.getInstance().setCacheMapTileCount((short) 12);
        Configuration.getInstance().setCacheMapTileOvershoot((short) 12);
        // Create a custom tile source
        map.setTileSource(new OnlineTileSourceBase("", 1, 20, 512, ".png",
                new String[]{"https://a.tile.openstreetmap.org/"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });

        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        GeoPoint startPoint;
        startPoint = new GeoPoint(41.456, 2.2);
        mapController.setZoom(16.0);
        mapController.setCenter(startPoint);
        final Context context = this;
        map.invalidate();
        createmarker();

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              finish();
            }
        });
    }

    public void createmarker() {
        if (map == null) {
            return;
        }
        Marker m_marker = new Marker(map);
        m_marker.setPosition(new GeoPoint(41.456, 2.19));
        m_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m_marker.setTitle("tatooCastellar");

        // Set custom info window with the ImageView
        CustomMarkerInfoWindow infoWindow = new CustomMarkerInfoWindow(map);
        m_marker.setInfoWindow(infoWindow);

        map.getOverlays().add(m_marker);

        // Create custom view for the marker popup
        View markerView = getLayoutInflater().inflate(R.layout.custom_marker_layout, null);
        ImageView markerImage = markerView.findViewById(R.id.marker_image);
        TextView markerTitle = markerView.findViewById(R.id.marker_title);

        // Load image with Glide
        Glide.with(this)
                .load("https://i.pinimg.com/736x/df/df/f4/dfdff4d065630cb8081f27c577f5f513.jpg")
                .into(markerImage);

        // Set title
        markerTitle.setText("TatooCastellar");

        // Create marker and set custom view
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(41.456, 2.23));
        marker.setInfoWindow(infoWindow);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("TatooCastellar Puig Castellar");


        Marker my_marker = new Marker(map);
        my_marker.setPosition(new GeoPoint(41.456, 2.2));
        my_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        my_marker.setTitle("Institut Puig Castellar");

        Marker marker1 = new Marker(map);
        marker1.setPosition(new GeoPoint(41.441, 2.213));
        marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker1.setTitle("Tatoo Studio Santa Coloma");

        Marker marker2 = new Marker(map);
        marker2.setPosition(new GeoPoint(41.441, 2.225));
        marker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker2.setTitle("Otro lugar");

        Marker marker3 = new Marker(map);
        marker3.setPosition(new GeoPoint(41.449, 2.221));
        marker3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker3.setTitle("Ink Tatoo");

        Marker marker4 = new Marker(map);
        marker4.setPosition(new GeoPoint(41.432, 2.215));
        marker4.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker4.setTitle("StudioTatoo SantAdri");



        // Load image with Glide
        ImageView imageView = new ImageView(this);
        Glide.with(this)
                .load("https://www.example.com/image.jpg")
                .into(imageView);

        // Set info window with the ImageView
        map.getOverlays().add(my_marker);
        map.invalidate();
        my_marker.setPanToView(true);
        map.getOverlays().add(marker);
        map.getOverlays().add(my_marker);
        map.getOverlays().add(marker1);
        map.getOverlays().add(marker2);
        map.getOverlays().add(marker3);
        map.getOverlays().add(marker4);

        map.invalidate();
    }
    public class CustomMarkerInfoWindow extends MarkerInfoWindow {
        private ImageView imageView;

        public CustomMarkerInfoWindow(MapView mapView) {
            super(R.layout.custom_marker_info_window, mapView);
            imageView = (ImageView) mView.findViewById(R.id.image_view);
        }

        @Override
        public void onOpen(Object item) {
            if (item instanceof Marker) {
                Marker marker = (Marker) item;
                Glide.with(imageView.getContext())
                        .load("https://i.pinimg.com/736x/df/df/f4/dfdff4d065630cb8081f27c577f5f513.jpg").circleCrop()
                        .into(imageView);
            }
        }
    }

}
