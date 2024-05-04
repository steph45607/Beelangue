package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LearnExploreActivity extends AppCompatActivity {

    ImageButton backButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_explore);

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

    }
}