package com.rk.amii.activities;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.rk.amii.R;
import com.rk.amii.databinding.ActivityImageViewBinding;

public class ImageViewActivity extends AppCompatActivity {

    private ActivityImageViewBinding binding;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int top = status.top;
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

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