package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    Button profileBtn, holderBtn;
    Button flagBtn;
    ArrayList<cardData> source;

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

//        getFlagImage();

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
                        i.putExtra("selected_language", "chinese");
                        startActivity(i);
                    }
                }
        );


//        flagBtn = findViewById(R.id.flagBtn);
//        flagBtn.setText("language");
//        flagBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.belangue_favicon_color,0,0,0);
    }


//    private void getFlagImage(){
////        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
////        StorageReference storageReference = firebaseStorage.getReference("flags/");
//        for(cardData card: source){
//            String source = "R.id" + card.language + "Btn";
////            flagBtn = findViewById(source);
////            StorageReference imageRef = storageReference.child("flags/" + card.country + ".png");
////            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
////               flagBtn.setCompoundDrawablesWithIntrinsicBounds(uri,0,0,0);
////            });
//
//        }
//        for(cardData card : source){
//            Log.d("languageDB", card.language + " - " + card.country);
//        }
//    }




}

