package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    Button loginButton, createButton ;
    EditText emailText, passwordText, usernameText;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(CreateAccountActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
        );

        createButton = findViewById(R.id.createBtn);
        emailText = findViewById(R.id.editEmail);
        passwordText = findViewById(R.id.editPassword);
        usernameText = findViewById(R.id.editUsername);

        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                registerNewUser();
            }
        });

    }

    private void registerNewUser(){
        String email, password, username;
        email = emailText.getText().toString();
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();

        // Email validation regex
        String emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (!email.matches(emailRegex)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter a valid email address!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter username!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Check if email is already in use
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            assert errorMessage != null;
                            if (errorMessage.contains("email address is already in use")) {
                                Toast.makeText(getApplicationContext(),
                                                "Email is already in use",
                                                Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                // Registration failed
                                Toast.makeText(getApplicationContext(),
                                                "Registration failed!! Please try again later",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                });
    }
}