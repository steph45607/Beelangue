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

        wordText = findViewById(R.id.wordText);
        titleText = findViewById(R.id.deckTitle);
        words = new ArrayList<>();

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
                Log.d("deckDB", "button clicked");
                saveDeckToDatabase();
                Intent i = new Intent(CreateDeckActivity.this, LearnActivity.class);
                startActivity(i);
            }
        });

    }

    private void addWordToList(){
        String word;
        word = wordText.getText().toString();
        words.add(word);
        wordText.setText("");
        Log.d("deckDB", "word - "+ word);
    }

    private void saveDeckToDatabase(){
        Log.d("deckDB", "save button clicked");
        String title;
        title = titleText.getText().toString();
        Log.d("deckDB", title + " " + words.get(0));
        deckData deck = new deckData(title, words);
        Log.d("deckDB", "deck object created, not saved");
//        Log.d("deckDB", deck.toString());

        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("deck");
        Log.d("deckDB", "database reference defined");
//        final String pushID = ref.push().getKey();
//        DatabaseReference newRef = ref.push();
        ref.push().setValue(deck)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("deckDB", "Data added to database");
                        Toast.makeText(getApplicationContext(), "Your achievement has been updated.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
