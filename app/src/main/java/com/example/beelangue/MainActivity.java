package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button profileBtn;
    ImageButton indo, france, vietnam, spain, italy, russia;
    Button flagBtn;
    ArrayList<cardData> source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        // initialize firebase database reference
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");

        // add listener to read data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // initiliaze data source list
                source = new ArrayList<>();

                // iterate through the data snapshot to extract country and language information
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String country = snapshot.child("country").getValue(String.class);
                    String lang = snapshot.child("language").getValue(String.class);
                    cardData key = new cardData(country, lang);
                    source.add(key);
                    Log.d("languageDB", "Value is: " + key.language);

                }
                Log.d("languageDB", "done reading DB");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("languageDB", "Failed to read value.", error.toException());
            }
        });
        Log.d("languageDB", "source received");

        // define profile button and set click listener
        profileBtn = findViewById(R.id.profile);
        profileBtn.setOnClickListener(
                v -> {
                    // navigate to ProfileActivity when profile button is clicked
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);
                }
        );

        // Initialize and set click listener for Indonesian flag
        indo = findViewById(R.id.indonesiaFlag);
        indo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "indonesia");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for French flag
        france = findViewById(R.id.franceFlag);
        france.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "french");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Vietnamese flag
        vietnam = findViewById(R.id.vietnamFlag);
        vietnam.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "vietnamese");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Spanish flag
        spain = findViewById(R.id.spainFlag);
        spain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "spanish");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Italian flag
        italy = findViewById(R.id.italyFlag);
        italy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "italian");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Russian flag
        russia = findViewById(R.id.russiaFlag);
        russia.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "russian");
                        startActivity(i);
                    }
                }
        );
    }
}

