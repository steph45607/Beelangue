package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class LearnExploreActivity extends AppCompatActivity {

    ImageButton backButton, exploreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_explore);
        FirebaseApp.initializeApp(this);

        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnExploreActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
        );

        exploreButton = findViewById(R.id.exploreBtn);
        exploreButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnExploreActivity.this, CameraPreview.class);
                        startActivity(i);
                    }
                }
        );

    }
}