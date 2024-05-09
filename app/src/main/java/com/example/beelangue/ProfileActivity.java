package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    ImageButton backBtn;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
        );
        logoutBtn = findViewById(R.id.logout);

        logoutBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
        );

    }
}
