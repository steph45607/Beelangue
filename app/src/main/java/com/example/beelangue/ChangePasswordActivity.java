package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        oldPasswordEditText = findViewById(R.id.editTextOldPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        Button saveButton = findViewById(R.id.buttonSave);
        ImageButton back = findViewById(R.id.backButton);
        mAuth = FirebaseAuth.getInstance();

        // Set up the back button to navigate to the ProfileActivity
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
        );

        // Set up the save button to trigger password change
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    // Method to handle password change
    private void changePassword() {
        // Get the old and new password inputs
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        // Validate the inputs
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(getApplicationContext(), "Enter your old password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getApplicationContext(), "Enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(getApplicationContext(), "New password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the currently signed-in user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Create a credential using the old password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            // Re-authenticate the user with the old password
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // If re-authentication is successful, update the password
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error password not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect old password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
