package com.example.blackphototatoo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ObjetoImagenAdapter extends RecyclerView.Adapter<ObjetoImagenAdapter.MyViewHolder> {
    private List<ObjetoImagen> objetoImagenes;


    public ObjetoImagenAdapter(List<ObjetoImagen> myEmails) {
        this.objetoImagenes = myEmails;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.objeto_imagen_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ObjetoImagen myObject = objetoImagenes.get(position);
        holder.imageView.setImageResource(myObject.getImageResource());
        holder.name.setText(myObject.getName());
        holder.fecha.setText(myObject.getFecha());
        holder.ranking.setText(myObject.getText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EmailActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return objetoImagenes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView ranking;
        public TextView fecha;
        public TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            ranking = itemView.findViewById(R.id.rankingfoto);
            fecha = itemView.findViewById(R.id.hora_creacion);
            name = itemView.findViewById(R.id.nombre_imagen);


        }
    }
}

