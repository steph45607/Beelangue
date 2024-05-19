package com.example.beelangue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    ImageButton backBtn;
    Button changeUsernameBtn;
    Button logoutBtn;
    Button deleteAccountBtn;
    FirebaseAuth mAuth;
    TextView usernameTextView;

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

        usernameTextView = findViewById(R.id.textView12);
        setUsername();

        changeUsernameBtn = findViewById(R.id.changeUsername);
        changeUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUsernameDialog();
            }
        });

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
                        Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                        finishAffinity();
                    }
                }
        );
    }

    private void setUsername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.getValue(String.class);
                        if (username != null) {
                            usernameTextView.setText(username);
                        }
                    } else {
                        Log.e("ProfileActivity", "Username does not exist.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ProfileActivity", "Failed to read username.", databaseError.toException());
                }
            });
        }
    }

    private void ChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.changeusername, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = viewInflated.findViewById(R.id.editTextUsername);
        builder.setView(viewInflated);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String newUsername = input.getText().toString().trim();
                if (!newUsername.isEmpty()) {
                    updateUsername(newUsername);
                } else {
                    Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateUsername(final String newUsername) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("username").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        usernameTextView.setText(newUsername);
                        Toast.makeText(getApplicationContext(), "Username updated", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to update username", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
                                    finishAffinity();
                                } else {
                                    // Failure
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                    Log.e("delete error", "Failed to delete account: " + errorMessage);
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
