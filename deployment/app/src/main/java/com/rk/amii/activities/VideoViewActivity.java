package com.rk.amii.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.rk.amii.R;

public class VideoViewActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        // Get video url from previous activity
        Intent intent = getIntent();
        String videoURL = intent.getStringExtra("videoURL");

        // Hide the action bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){
            System.out.println(e);
        }

        // Get the video view and set the video URL
        videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(videoURL);
        videoView.seekTo(1);

        // Create a media controller and bond it to the video view
        MediaController introMediaController = new MediaController(this);
        introMediaController.setAnchorView(videoView);
        videoView.setMediaController(introMediaController);

    }
}