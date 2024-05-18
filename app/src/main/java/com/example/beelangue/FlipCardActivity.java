package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlipCardActivity extends AppCompatActivity {
    Button flipButton;
    Integer currentIndex, indexSize;
    String currentText;
    ImageButton prevButton, nextButton, backBtn;

    Boolean keyStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_page);
//        Log.d("koesmanto", "card page hehe");

        Intent intent = getIntent();
        deckData deck = intent.getParcelableExtra("deck");
//        Log.d("koesmanto", "card page intent call");

        Map<String, String> dictionary = deck.wordDict;
//        Log.d("koesmanto", deck.name);
//        Log.d("koesmanto", deck.words.toString());
        Log.d("koesmanto", dictionary.toString());

        String selectedLanguage = getIntent().getStringExtra("selected_language");
//        Log.d("koesmanto", "string language intent call");

        flipButton = findViewById(R.id.flipBtn);
//        currentIndex = deck.words.size();

        currentIndex = 0;
        Set<String> keys = dictionary.keySet();
        List<String> keyList = new ArrayList<>(keys);
        indexSize = keys.size();

        flipButton.setText(keyList.get(0));
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FlipButton", "flipped - " + currentIndex + " " + indexSize);
                if(keyStatus) {
                    currentText = keyList.get(currentIndex);
                    flipButton.setText(dictionary.get(currentText));
                    keyStatus = false;
                }
                else{
                    currentText = keyList.get(currentIndex);
                    flipButton.setText(currentText);
                    keyStatus = true;
                };
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

        nextButton = findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FlipButton", "next clicked - " + currentIndex + " " + currentText);
                if (currentIndex >= 0 && currentIndex < indexSize-1) {
                    currentIndex++;
                    currentText = keyList.get(currentIndex);
                    flipButton.setText(currentText);
                    keyStatus = true;
                }else{
                    currentIndex = 0;
                    currentText = keyList.get(currentIndex);
                    flipButton.setText(currentText);
                    keyStatus = true;
                }
            }
        });

        prevButton = findViewById(R.id.previousBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FlipButton", "previous clicked - " + currentIndex + " " + currentText);
                if (currentIndex > 0 || currentIndex.equals(indexSize-1)) {
                    currentIndex--;
                    currentText = keyList.get(currentIndex);
                    flipButton.setText(currentText);
                    keyStatus = true;
                }else{
                    currentIndex = indexSize-1;
                    currentText = keyList.get(currentIndex);
                    flipButton.setText(currentText);
                    keyStatus = true;
                }
            }
        });
    }

}
