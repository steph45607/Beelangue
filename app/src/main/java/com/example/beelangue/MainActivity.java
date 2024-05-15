package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ArrayList<cardData> source = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    String name = snapshot.getKey();
                    String country = snapshot.child("country").getValue(String.class);
                    Integer id = snapshot.child("id").getValue(Integer.class);
                    cardData key = new cardData(country, id);
                    source.add(key);
//                    Log.d("languageDB", key.country + " " + key.id );
                }
//                Log.d("languageDB", "Value is: " + source.get(0).id);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("languageDB", "Failed to read value.", error.toException());
            }
        });

        profileBtn = findViewById(R.id.profile);
        profileBtn.setOnClickListener(
                v -> {
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);
                }
        );

//        flagBtn = findViewById(R.id.japanese);
//        flagBtn.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
//                        startActivity(i);
//                    }
//                }
//        );
    }

}

