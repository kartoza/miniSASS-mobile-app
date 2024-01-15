package com.rk.amii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rk.amii.R;
import com.rk.amii.activities.ImageViewActivity;
import com.rk.amii.models.PhotoModel;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PhotoModel> photos;

    public PhotoAdapter(Context context, ArrayList<PhotoModel> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item,
                parent, false));
    }

    /**
     * Bind view holder
     * @param holder
     * @param position
     */
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        PhotoModel modal = photos.get(position);
        holder.userChoice.setText(modal.getUserChoice().toString());
        holder.mlPrediction.setText(modal.getMlPredictions().toString());
        Bitmap bitmap = BitmapFactory.decodeFile(modal.getPhotoLocation());
        holder.bitmap.setImageBitmap(bitmap);

        holder.bitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ImageViewActivity.class);
                i.putExtra("image", modal.getPhotoLocation());
                context.startActivity(i);
            }
        });
    }

    /**
     * Get the number of photos
     * @return number of photos
     */
    public int getItemCount() {
        return photos.size();
    }

    /**
     * Set the view holder values
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bitmap;
        private TextView userChoice;
        private TextView mlPrediction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bitmap = itemView.findViewById(R.id.idPhotoBitmap);
            userChoice = itemView.findViewById(R.id.idUserChoice);
            mlPrediction = itemView.findViewById(R.id.idMlPrediction);
        }
    }

}
