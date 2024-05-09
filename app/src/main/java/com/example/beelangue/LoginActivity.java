package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    Button createAccountBtn, loginButton  ;
    EditText emailText, passwordText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        createAccountBtn = findViewById(R.id.create_button);
        createAccountBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
                        startActivity(i);
                    }
                }
        );
        loginButton = findViewById(R.id.login_button);
        emailText = findViewById(R.id.editUsername); // to be edited, use email
        passwordText = findViewById(R.id.editPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.print("hello");
                        Log.d("LoginActivity", "signInWithEmailAndPassword: " + (task.isSuccessful() ? "success" : "failure"));
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Login failed
                            Toast.makeText(getApplicationContext(), "Login failed! Please check your credentials.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}