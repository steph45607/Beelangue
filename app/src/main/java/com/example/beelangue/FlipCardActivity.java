package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FlipCardActivity extends AppCompatActivity {
    Button flipButton;
    ImageButton prevButton, nextButton, backBtn;

//    make a function to get the words from database

//    make a function to translate the word list to dictionary where key is english and value is translated word
//      chair: kursi (example)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_page);

//        Intent intent = getIntent();
//        deckData deck = intent.getParcelableExtra("deck");
//        Log.d("koesmanto", deck.words.toString() + " from card page hehe");

        flipButton = findViewById(R.id.flipBtn);
        flipButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("koesmanto", "button flip clicked");
            }
        });

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(FlipCardActivity.this, LearnActivity.class);
                startActivity(intent);
            }
        });
    }

}
