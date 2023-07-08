package com.example.imagepro;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class FullScreenImage extends AppCompatActivity {

    Toolbar toolbar;
    public static final String EXTRA_PHOTO_RES_ID = "extra_photo_res_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        toolbar = findViewById(R.id.pre);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Numbers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = findViewById(R.id.fullScreenImageView);

        // Get the photo resource ID from the intent
        int photoResId = getIntent().getIntExtra(EXTRA_PHOTO_RES_ID, 0);

        // Set the photo to the ImageView
        imageView.setImageResource(photoResId);
    }
}