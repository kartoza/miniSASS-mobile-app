package com.rk.amii.activities;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.rk.amii.R;
import com.rk.amii.databinding.ActivityImageViewDescriptionBinding;

public class ImageViewDescriptionActivity extends AppCompatActivity {

    private ActivityImageViewDescriptionBinding binding;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int top = status.top;
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

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