package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class CreateDeckActivity extends AppCompatActivity {

    Button createDeckBtn, addBtn;
    ImageButton backButton;
    EditText titleText, wordText;
    ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_deck);

        addBtn = findViewById(R.id.addCardBtn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addWordToList();
            }
        });

        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(CreateDeckActivity.this, LearnActivity.class);
                        startActivity(i);
                    }
                }
        );

        createDeckBtn = findViewById(R.id.finishDeckBtn);
        createDeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDeckToDatabase();
            }
        });

    }

    private void addWordToList(){
        String word;
        word = wordText.getText().toString();
        words.add(word);
        wordText.setText("");
    }

    private void saveDeckToDatabase(){
        String title;
        title = titleText.getText().toString();
        deckData deck = new deckData(title, words);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("decks");

        final String pushID = ref.push().getKey();
        Log.d("deckDB", pushID);
        FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("decks").child(pushID).setValue(deck)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("deckDB", "saved " + deck.name + " " + deck.words + " " + deck.length);
                        Toast.makeText(getApplicationContext(), "Your achievement has been updated.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
