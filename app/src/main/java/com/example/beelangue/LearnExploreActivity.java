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
        // Get the selected language passed from the previous activity
        String selectedLanguage = getIntent().getStringExtra("selected_language");
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize and set click listener for the back button
        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate back to MainActivity when back button is clicked
                        Intent i = new Intent(LearnExploreActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for the explore button
        exploreButton = findViewById(R.id.exploreBtn);
        exploreButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Initialize and set click listener for the explore button
                        Intent i = new Intent(LearnExploreActivity.this, CameraPreview.class);
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for the learn button
        learnButton = findViewById(R.id.learnBtn);
        learnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to LearnActivity with the selected language
                        Intent i = new Intent(LearnExploreActivity.this, LearnActivity.class);
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                    }
                }
        );

    }
}