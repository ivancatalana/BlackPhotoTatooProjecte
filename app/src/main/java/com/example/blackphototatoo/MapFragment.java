package com.example.blackphototatoo;

import android.os.Bundle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;


public class MapFragment extends Fragment {

    private MapView mapView;
    private NavController navController;
    private MapController mapController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Configuration.getInstance().load(getContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()));
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);


        mapController = (MapController) mapView.getController();
        mapController.setZoom(17);
        mapController.setCenter(new GeoPoint(41.3851, 2.1734)); // Barcelona coordinates
        // Add rotation gesture overlay
        RotationGestureOverlay rotationOverlay = new RotationGestureOverlay(mapView);
        rotationOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationOverlay);
        mapView.setMultiTouchControls(true);
        GeoPoint startPoint;
        startPoint = new GeoPoint(41.456, 2.2);
        mapController.setZoom(16.0);
        mapController.setCenter(startPoint);
        mapView.invalidate();
        createmarker();
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDetach();
    }

    public void createmarker() {
        if (mapView == null) {
            return;
        }
        Marker m_marker = new Marker(mapView);
        m_marker.setPosition(new GeoPoint(41.456, 2.19));
        m_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m_marker.setTitle("tatooCastellar");

        // Set custom info window with the ImageView
        CustomMarkerInfoWindow infoWindow = new CustomMarkerInfoWindow(mapView);
        m_marker.setInfoWindow(infoWindow);


        mapView.getOverlays().add(m_marker);

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
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(41.456, 2.23));
        marker.setInfoWindow(infoWindow);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("TatooCastellar Puig Castellar");


        Marker my_marker = new Marker(mapView);
        my_marker.setPosition(new GeoPoint(41.456, 2.2));
        my_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        my_marker.setTitle("Institut Puig Castellar");

        Marker marker1 = new Marker(mapView);
        marker1.setPosition(new GeoPoint(41.441, 2.213));
        marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker1.setTitle("Tatoo Studio Santa Coloma");

        Marker marker2 = new Marker(mapView);
        marker2.setPosition(new GeoPoint(41.441, 2.225));
        marker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker2.setTitle("Otro lugar");

        Marker marker3 = new Marker(mapView);
        marker3.setPosition(new GeoPoint(41.449, 2.221));
        marker3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker3.setTitle("Ink Tatoo");

        Marker marker4 = new Marker(mapView);
        marker4.setPosition(new GeoPoint(41.432, 2.215));
        marker4.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker4.setTitle("StudioTatoo SantAdri");



        // Load image with Glide
        ImageView imageView = new ImageView(this.getContext());
        Glide.with(this)
                .load("https://www.example.com/image.jpg")
                .into(imageView);

        // Set info window with the ImageView
        mapView.getOverlays().add(my_marker);
        mapView.invalidate();
        my_marker.setPanToView(true);
        mapView.getOverlays().add(marker);
        mapView.getOverlays().add(my_marker);
        mapView.getOverlays().add(marker1);
        mapView.getOverlays().add(marker2);
        mapView.getOverlays().add(marker3);
        mapView.getOverlays().add(marker4);

        mapView.invalidate();
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