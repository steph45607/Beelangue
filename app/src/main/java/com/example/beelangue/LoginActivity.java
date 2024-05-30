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
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    Button createAccountBtn, loginButton, resetPasswordButton;
    EditText emailText, passwordText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize Firebase Authentication
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Create Account button click listener
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
        resetPasswordButton = findViewById(R.id.resetButton);
        emailText = findViewById(R.id.editUsername); // to be edited, use email
        passwordText = findViewById(R.id.editPassword);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Reset Password button click listener
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (!currentUser.isEmailVerified()){
                cancelRegistration();
            } else {
                // If logged in, navigate to MainActivity
                Log.d("DB", "onDataChange called" + currentUser.getUid());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    // delete previous user if email is not verified
    private void cancelRegistration() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Delete user data from database
            mDatabase.child("users").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // User data deleted, now delete the user account
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Account deleted
                                    Toast.makeText(getApplicationContext(), "Registration cancelled", Toast.LENGTH_LONG).show();
                                } else {
                                    // Failure
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                    Log.e("delete account", "Failed to delete account: " + errorMessage);
                                    Toast.makeText(getApplicationContext(), "Failed to delete account", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to delete user data", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // Method to log in the user
    private void loginUser() {
        String input = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Check for empty fields
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(getApplicationContext(), "Please enter email or username!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        // Determine if the input is an email or username and proceed with login accordingly
        if (isEmail(input)) {
            // Login with email
            signInWithEmail(input, password);
        } else {
            // Login with username
            signInWithUsername(input, password);
        }
    }

    // Method to check if the input is an email
    private boolean isEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // Method to sign in with email
    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("login", "signInWithEmailAndPassword: " + (task.isSuccessful() ? "success" : "failure"));
                        // Method to sign in with email
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Login failed
                            Toast.makeText(getApplicationContext(), "Login failed! Please check your email address and password!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to sign in with username
    private void signInWithUsername(String username, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("users");

        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If username exists, retrieve associated email and proceed with email login
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String email = snapshot.child("email").getValue(String.class);
                        signInWithEmail(email, password);
                        break;
                    }
                } else {
                    // Username not found
                    Toast.makeText(getApplicationContext(), "Login failed! Please check your username and password!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Database error
                Log.w("login", "Failed sign in with username", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Database error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to reset password
    private void resetPassword() {
        String input = emailText.getText().toString();

        // Check for empty field
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(getApplicationContext(), "Please enter your email or username!", Toast.LENGTH_LONG).show();
            return;
        }

        if (isEmail(input)) {
            sendResetEmail(input);
        } else {
            fetchUsernameAndReset(input);
        }
    }

    // Method to send reset email
    private void sendResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Password reset email sent!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send password reset email!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to fetch username and reset password
    private void fetchUsernameAndReset(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("users");

        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String email = snapshot.child("email").getValue(String.class);
                        sendResetEmail(email);
                        break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username not found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("login", "Failed to fetch email by username", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Database error!", Toast.LENGTH_LONG).show();
            }
        });
    }
}