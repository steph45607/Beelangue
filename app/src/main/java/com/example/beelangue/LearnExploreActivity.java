package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class LearnExploreActivity extends AppCompatActivity {

    ImageButton backButton, exploreButton, learnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_explore);
        String selectedLanguage = getIntent().getStringExtra("selected_language");
        FirebaseApp.initializeApp(this);

        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnExploreActivity.this, MainActivity.class);
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
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                    }
                }
        );

        learnButton = findViewById(R.id.learnBtn);
        learnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnExploreActivity.this, LearnActivity.class);
                        startActivity(i);
                    }
                }
        );

    }
}