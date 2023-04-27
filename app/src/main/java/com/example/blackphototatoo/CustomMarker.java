package com.example.blackphototatoo;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
//import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
public class CustomMarker extends Marker {

    private Context mContext;
    private Bitmap mBitmap;

    public CustomMarker(MapView mapView, Context context) {
        super(mapView);
        mContext = context;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

 //   @Override
    public boolean onBubbleClickListener() {
        // Create a custom view to hold the image
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(mBitmap);

        // Set the view as the content of the bubble
 //       setInfoWindow(new DefaultInfoWindow(imageView, this));
        return true;
    }
}
