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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_page);
        Log.d("koesmanto", "card page hehe");

//        DictionaryWrapper

        Intent intent = getIntent();
        deckData deck = intent.getParcelableExtra("deck");
        Log.d("koesmanto", "card page intent call");

//        Map<String, String> dictionary = deck.wordDict;
        Log.d("koesmanto", deck.name);
        Log.d("koesmanto", deck.words.toString());
        Log.d("koesmanto", "deck is "+ deck.wordDict);

//        Map<String, String> dictionary = deck.wordDict;
//        Log.d("koesmanto", dictionary);


        String selectedLanguage = getIntent().getStringExtra("selected_language");
        Log.d("koesmanto", "string language intent call");

        flipButton = findViewById(R.id.flipBtn);
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("koesmanto", "button flip clicked");
            }
        });

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlipCardActivity.this, LearnActivity.class);
                intent.putExtra("selected_language", selectedLanguage);
                startActivity(intent);
            }
        });
    }

}
