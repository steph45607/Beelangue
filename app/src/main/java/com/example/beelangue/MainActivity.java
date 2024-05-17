package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button profileBtn, holderBtn, flagBtn;
    ArrayList<cardData> source;
    ArrayList<String> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                source = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String country = snapshot.child("country").getValue(String.class);
                    Integer id = snapshot.child("id").getValue(Integer.class);
                    cardData key = new cardData(country, id);
                    source.add(key);
                }
                Log.d("languageDB", "Value is: " + source.get(0).id);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("languageDB", "Failed to read value.", error.toException());
            }
        });
        Log.d("activityStatus", "source received");

        getLanguageList();
        getFlagImage();

        profileBtn = findViewById(R.id.profile);
        profileBtn.setOnClickListener(
                v -> {
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);
                }
        );

        holderBtn = findViewById(R.id.holder);
        holderBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        startActivity(i);
                    }
                }
        );


        flagBtn = findViewById(R.id.flagBtn);
//        flagBtn.setText("language");
//        flagBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.belangue_favicon_color,0,0,0);
    }

    private void getLanguageList(){
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getKey();
                languages.add(value);
                Log.d("languageDB", languages.toString() + " " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFlagImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        for(String language : languages){
            Log.d("languageDB", language);
        }
        flagBtn.setText(languages.get(0));
//        flagBtn.setCompoundDrawablesWithIntrinsicBounds(image,0,0,0);
    }




}

