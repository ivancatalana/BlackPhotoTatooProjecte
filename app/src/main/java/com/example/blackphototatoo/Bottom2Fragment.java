package com.example.blackphototatoo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImage;
/*
public class Bottom2Fragment extends Fragment {
    private GPUImage gpuImage;
    private ImageView imageView;
    private SeekBar brightnessSeekBar;
    private SeekBar contrastSeekBar;
    private Uri imageUri;


    public Bottom2Fragment() {
        // constructor vacío requerido
    }

    public Bottom2Fragment(Uri imageUri) {
        this.imageUri = imageUri;
    }

 */
    public class Bottom2Fragment extends Fragment {

        private GPUImage gpuImage;
        private ImageView imageView;
        private SeekBar brightnessSeekBar;
        private SeekBar contrastSeekBar;

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_bottom2, container, false);

            gpuImage = new GPUImage(getContext());
            imageView = view.findViewById(R.id.imageView);
            brightnessSeekBar = view.findViewById(R.id.brightnessSeekBar);
            contrastSeekBar = view.findViewById(R.id.contrastSeekBar);

            // Configurar imagen inicial
            // Obtener el Drawable del archivo drawable
            Drawable drawable = getResources().getDrawable(R.drawable.profile);

           // Convertir el Drawable en Bitmap
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            // Establecer el Bitmap en el GPUImageView
            gpuImage.setImage(bitmap);

            // Actualizar imagen al cambiar brillo
            brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateBrightness(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    comprimirImagen(gpuImage.getBitmapWithFilterApplied());
                }
            });

            // Actualizar imagen al cambiar contraste
            contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateContrast(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    comprimirImagen(gpuImage.getBitmapWithFilterApplied());
                }
            });

            return view;
        }

        private void updateBrightness(int brightness) {
            gpuImage.setFilter(new GPUImageBrightnessFilter(brightness / 100f));
            imageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
        }

        private void updateContrast(int contrast) {
            gpuImage.setFilter(new GPUImageContrastFilter(contrast / 100f));
            imageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
        }

        private void comprimirImagen(Bitmap bitmap) {
            new CompressImageTask(bitmap).execute();
        }

        private class CompressImageTask extends AsyncTask<Void, Void, Void> {

            private Bitmap bitmap;

            public CompressImageTask(Bitmap bitmap) {
                this.bitmap = bitmap;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                String fileName = "mi_imagen.jpg";
                FileOutputStream outputStream;
                try {
                    outputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Aquí puedes hacer algo después de que la compresión esté completa
            }
        }
    }
