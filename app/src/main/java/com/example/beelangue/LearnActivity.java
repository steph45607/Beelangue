package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LearnActivity extends AppCompatActivity {

    ImageButton back, create;
    MaterialButton create1;
    LinearLayout buttonContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_page);

        back = findViewById(R.id.backBtn);
        create = findViewById(R.id.createBtn);
        create1 = findViewById(R.id.createDeck);
        buttonContainer = findViewById(R.id.buttonContainer);

        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnActivity.this, LearnExploreActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
        );

//        create.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(LearnActivity.this, CreateDeckActivity.class);
//                        startActivity(i);
//                    }
//                }
//        );

        create1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnActivity.this, CreateDeckActivity.class);
                        startActivity(i);
                    }
                }
        );

        fetchDecks();
    }

    private void fetchDecks() {
        Log.d("koesmanto", "fetch deck called");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("deck");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("koesmanto", "deck gotten called");
                buttonContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String deckName = snapshot.child("name").getValue(String.class);
                    Log.d("koesmanto", deckName);
                    createDeckButton(deckName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LearnActivity.this, "Failed to load decks.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDeckButton(final String deckName) {
        Button button = new Button(this);
        button.setText(deckName);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("koesmanto", "clicked"+v.toString());
//                Intent intent = new Intent(LearnActivity.this, deckthingy.class);
//                intent.putExtra("DECK_NAME", deckName);
//                startActivity(intent);
            }
        });
        buttonContainer.addView(button);
    }
}
