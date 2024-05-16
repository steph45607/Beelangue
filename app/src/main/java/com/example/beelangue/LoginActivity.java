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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("koesmanto", "onDataChange called"+ currentUser.getUid());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String input = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (TextUtils.isEmpty(input)) {
            Toast.makeText(getApplicationContext(), "Please enter email or username!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        if (isEmail(input)) {
            // Login with email
            signInWithEmail(input, password);
        } else {
            // Login with username
            signInWithUsername(input, password);
        }
    }

    private boolean isEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("koesmanto", "signInWithEmailAndPassword: " + (task.isSuccessful() ? "success" : "failure"));
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Login failed
                            Toast.makeText(getApplicationContext(), "Login failed! Please check your email address and password!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInWithUsername(String username, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("users");

        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String email = snapshot.child("email").getValue(String.class);
                        signInWithEmail(email, password);
                        break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Login failed! Please check your username and password!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("koesmanto", "Failed sign in with username", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Database error!", Toast.LENGTH_LONG).show();
            }
        });
    }
}