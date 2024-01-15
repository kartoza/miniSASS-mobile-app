package com.rk.amii.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rk.amii.R;
import com.rk.amii.activities.ImageViewDescriptionActivity;

import java.util.ArrayList;

public class BitmapAdapter extends RecyclerView.Adapter<BitmapAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Integer> images;
    private ArrayList<Integer> bimages;
    private Integer description;

    public BitmapAdapter(Context context, ArrayList<Integer> images, ArrayList<Integer> bimages, Integer description) {
        this.context = context;
        this.images = images;
        this.description = description;
        this.bimages = bimages;
    }

    @NonNull
    @Override
    public BitmapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BitmapAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.bitmap_item,
                parent, false));
    }

    public void onBindViewHolder(@NonNull BitmapAdapter.ViewHolder holder, int position) {
        int bitmap = images.get(position);
        int bbitmap = bimages.get(position);
        holder.bitmapView.setImageResource(bitmap);

        holder.bitmapView.setOnClickListener(view -> {
            Intent i = new Intent(context, ImageViewDescriptionActivity.class);
            i.putExtra("image", bbitmap);
            i.putExtra("description", description);
            context.startActivity(i);
        });
    }

    public int getItemCount() {
        return this.images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bitmapView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bitmapView = itemView.findViewById(R.id.bitmapItem);
        }
    }

}
