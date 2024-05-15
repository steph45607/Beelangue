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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    Button loginButton, createButton;
    EditText emailText, passwordText, usernameText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

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

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

    }

    private void registerNewUser() {
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
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter username!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (username.length() < 3) {
            Toast.makeText(getApplicationContext(),
                            "Username must be at least 3 characters long!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Check if the username already exists
        mDatabase.child("users").orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("koesmanto", "onDataChange called");
                        if (snapshot.exists()) {
                            Log.d("koesmanto", "Username already exists: " + username);
                            Toast.makeText(getApplicationContext(),
                                            "Username already exists!!",
                                            Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            createFirebaseUser(email, password, username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("koesmanto", "Database error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                        "Database error. Please try again later",
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void createFirebaseUser(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            saveUsername(userId, username);

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

    private void saveUsername(String userId, String username) {
        Log.d("koesmanto", "Attempting to save username for user: " + userId);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);

        mDatabase.child("users").child(userId).setValue(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("koesmanto", "Username saved successfully for UID: " + userId);
                        } else {
                            // Failed
                            Toast.makeText(getApplicationContext(),
                                            "Failed to save username",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }
}