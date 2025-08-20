package com.rk.amii.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rk.amii.R;
import com.rk.amii.activities.VideoViewActivity;
import com.rk.amii.activities.WebActivity;
import com.rk.amii.models.VideoModel;

import java.util.ArrayList;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<VideoModel> videos;
    private String videoType;


    public VideoAdapter(Context context, ArrayList<VideoModel> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.video_item,
                parent, false));
    }

    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {

        VideoModel modal = videos.get(position);

        videoType = modal.getVideoType();

        VideoView videoView = holder.itemView.findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(modal.getVideoURL()));
        videoView.seekTo(1);

        ImageView onlineVideoView = holder.itemView.findViewById(R.id.videoOnlineView);

        if (videoType == "offline") {
            onlineVideoView.setVisibility(View.GONE);
        } else {
            videoView.setVisibility(View.GONE);
            onlineVideoView.setImageResource(modal.getVideoImage());
        }

        TextView videoTitle = holder.itemView.findViewById(R.id.videoTitle);
        videoTitle.setText(modal.getVideoTitle());



        holder.itemView.setOnClickListener(view -> {
            if (videoType == "offline") {
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtra("videoURL", modal.getVideoURL());
                context.startActivity(intent);
            } else {
                Uri uri = Uri.parse(modal.getVideoURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "text/html");
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                context.startActivity(intent);
            }

        });

    }

    public int getItemCount() {
        return this.videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView siteName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            siteName = itemView.findViewById(R.id.idSiteName);
        }
    }

}
