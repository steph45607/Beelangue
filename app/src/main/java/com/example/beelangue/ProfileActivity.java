package com.example.beelangue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    ImageButton backBtn;
    Button logoutBtn;
    Button deleteAccountBtn;
    FirebaseAuth mAuth;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

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

        deleteAccountBtn = findViewById(R.id.deleteAccount);
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountPrompt();
            }
        });

        logoutBtn = findViewById(R.id.logout);

        logoutBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAuth.signOut();
                        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                        finishAffinity();
                    }
                }
        );
    }

    // send alert for deleting
    private void deleteAccountPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel
                    }
                });
        // show alert
        builder.create().show();
    }

    // delete account if yes
    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Delete username from database
            mDatabase.child("users").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //  username deleted
                        //  delete the user account
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Account deleted
                                    Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Failure
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
