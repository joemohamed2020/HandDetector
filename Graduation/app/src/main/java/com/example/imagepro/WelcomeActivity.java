package com.example.imagepro;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;



public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ImageView arrowImg = findViewById(R.id.imageView3);
        arrowImg.setOnClickListener(view -> IntentClass.goToActivity(WelcomeActivity.this,SigninActivity.class));
    }
}