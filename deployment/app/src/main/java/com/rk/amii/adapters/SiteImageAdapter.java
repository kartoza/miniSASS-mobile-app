package com.rk.amii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rk.amii.R;
import com.rk.amii.activities.ImageViewActivity;

import java.util.ArrayList;

public class SiteImageAdapter extends RecyclerView.Adapter<SiteImageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images;

    public SiteImageAdapter(Context context, ArrayList<String> photos) {
        this.context = context;
        this.images = photos;
    }

    @NonNull
    @Override
    public SiteImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SiteImageAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.site_image_item,
                parent, false));
    }

    /**
     * Bind view holder
     * @param holder
     * @param position
     */
    public void onBindViewHolder(@NonNull SiteImageAdapter.ViewHolder holder, int position) {
        String location = images.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(location);
        holder.bitmap.setImageBitmap(bitmap);

        holder.bitmap.setOnClickListener(view -> {
            Intent i = new Intent(context, ImageViewActivity.class);
            i.putExtra("image", location);
            context.startActivity(i);
        });
    }

    /**
     * Get the number of photos
     * @return number of photos
     */
    public int getItemCount() {
        return images.size();
    }

    /**
     * Set the view holder values
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bitmap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bitmap = itemView.findViewById(R.id.idSiteImageBitmap);
        }
    }

}

