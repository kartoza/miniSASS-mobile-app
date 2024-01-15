package com.rk.amii.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rk.amii.R;
import com.rk.amii.activities.ImageViewActivity;
import com.rk.amii.models.SampleItemModel;
import com.rk.amii.activities.SiteDetailActivity;

import java.util.ArrayList;

public class SampleItemAdapter extends RecyclerView.Adapter<SampleItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SampleItemModel> sampleItems;

    public SampleItemAdapter(Context context, ArrayList<SampleItemModel> sampleItems) {
        this.context = context;
        this.sampleItems = sampleItems;
    }

    @NonNull
    @Override
    public SampleItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SampleItemAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_item,
                parent, false));
    }

    public void onBindViewHolder(@NonNull SampleItemAdapter.ViewHolder holder, int position) {
        SampleItemModel modal = sampleItems.get(position);
        ImageView image = (ImageView)holder.itemView.findViewById(R.id.bitmap);
        image.setImageBitmap(modal.getImage());

        TextView invertType = (TextView)holder.itemView.findViewById(R.id.invertType);
        invertType.setText(modal.getInvertType());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("bitmap", modal.getImage());
            intent.putExtra("invertType", modal.getInvertType());

            context.startActivity(intent);
        });
    }

    public int getItemCount() {
        return sampleItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
