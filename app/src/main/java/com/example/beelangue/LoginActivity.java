package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button createAccountBtn, loginButton  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        createAccountBtn = findViewById(R.id.createBtn);
        createAccountBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
                        startActivity(i);
                    }
                }
        );
        createAccountBtn = findViewById(R.id.loginBtn);
        createAccountBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, LearnExploreActivity.class);
                        startActivity(i);
                    }
                }
        );

    }
}