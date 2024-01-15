package com.rk.amii.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.rk.amii.R;

public class ImageViewDescriptionActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_description);

        // Get the image path and description from previous activity
        Intent intent = getIntent();
        int image = intent.getIntExtra("image", 0);
        int descriptionText = intent.getIntExtra("description", 0);

        // Hide the action bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){
            System.out.println(e);
        }

        // Get the image view and set the image
        imageView = findViewById(R.id.idFullImageDescriptionView);
        imageView.setImageResource(image);

        // Get the description view and set the description
        TextView description = findViewById(R.id.idGroupDescription);
        description.setText(descriptionText);
    }
}