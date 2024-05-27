package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText currentPasswordEditText;
    private EditText newEmailEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeemail);

        mAuth = FirebaseAuth.getInstance();

        currentPasswordEditText = findViewById(R.id.PasswordEditText);
        newEmailEditText = findViewById(R.id.newEmailEditText);
        Button changeEmailButton = findViewById(R.id.changeEmailButton);

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });
    }

    private void changeEmail() {
        final String currentPassword = currentPasswordEditText.getText().toString().trim();
        final String newEmail = newEmailEditText.getText().toString().trim();

        String emailRegex = "[a-zA-Z0-9._-]+@[a-z]+[\\.+[a-z]+]+";

        if (currentPassword.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(ChangeEmailActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newEmail.matches(emailRegex)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter a valid email address!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangeEmailActivity.this, "Verification email sent to update email address. Please check your email.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ChangeEmailActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ChangeEmailActivity.this, "Failed to send verification email", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChangeEmailActivity.this, "Authentication failed. Check your password and try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

