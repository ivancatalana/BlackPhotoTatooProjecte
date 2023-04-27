package com.example.blackphototatoo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<MyObject> myObjects;
    private NavController navController;
    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public MyAdapter(List<MyObject> myObjects) {
        this.myObjects = myObjects;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_object_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyObject myObject = myObjects.get(position);
        holder.imageView.setImageResource(myObject.getImageResource());
        holder.textView.setText(myObject.getText());

        holder.itemView.setOnClickListener(v -> {
            navController.navigate(R.id.postDiscoverFragment);
        });
    }

    @Override
    public int getItemCount() {
        return myObjects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.rankingfoto);
        }
    }

}

