package com.rk.amii.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.rk.amii.R;

public class ImageViewActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        // Get image path from previous activity
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");

        // Hide the actionbar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){
            System.out.println(e);
        }

        // Get the image view
        imageView = findViewById(R.id.idFullImageView);

        // Get the image bitmap from the image path and set
        // to the image view
        Bitmap bitmap = BitmapFactory.decodeFile(image);
        imageView.setImageBitmap(bitmap);
    }
}