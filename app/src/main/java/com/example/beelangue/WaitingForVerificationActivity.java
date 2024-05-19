package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaitingForVerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private Button resendEmailButton;

    // Handler for checking verification status
    private final Handler handler = new Handler();
    private final Runnable checkVerificationStatus = new Runnable() {
        @Override
        public void run() {
            mUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (mUser.isEmailVerified()) {
                        // Email verified, navigate to MainActivity
                        Toast.makeText(WaitingForVerificationActivity.this, "Email verified!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WaitingForVerificationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Not verified yet, check again after 3 seconds
                        handler.postDelayed(checkVerificationStatus, 3000);
                    }
                }
            });
        }
    };

    // Handler for enabling resend button after a timeout
    private Handler handlerResend = new Handler();
    private Runnable enableResendButtonRunnable = new Runnable() {
        @Override
        public void run() {
            // Enable resend button
            enableResendButton();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitingforverification);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        resendEmailButton = findViewById(R.id.resendEmailButton);
        Button changeEmailButton = findViewById(R.id.changeEmailButton);

        // Disable resend button initially and schedule re-enabling after 60 seconds
        disableResendButton();
        handlerResend.postDelayed(enableResendButtonRunnable, 60000);

        // Resend email button click listener
        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
            }
        });

        // Change email button click listener
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRegistration();
            }
        });

        // Start checking verification status
        handler.postDelayed(checkVerificationStatus, 3000);

        // Handle back press
        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cancelRegistration();
                Intent intent = new Intent(WaitingForVerificationActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Resend email verification
    private void resendVerificationEmail() {
        disableResendButton();
        handlerResend.postDelayed(enableResendButtonRunnable, 60000); // Re-enable after 60 seconds
        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(WaitingForVerificationActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ResendVerification", "Failed to send verification email. Error: " + task.getException());
                    Toast.makeText(WaitingForVerificationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Enable resend button
    private void enableResendButton() {
        resendEmailButton.setEnabled(true);
        resendEmailButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_enabled));
        resendEmailButton.setTextColor(ContextCompat.getColor(this, R.color.text_color_enabled));
    }

    // Disable resend button
    private void disableResendButton() {
        resendEmailButton.setEnabled(false);
        resendEmailButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_disabled));
        resendEmailButton.setTextColor(ContextCompat.getColor(this, R.color.text_color_disabled));
    }

    // Cancel user registration
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
                                    Intent intent = new Intent(WaitingForVerificationActivity.this, CreateAccountActivity.class);
                                    startActivity(intent);
                                    finish();
                                    finishAffinity();
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
}