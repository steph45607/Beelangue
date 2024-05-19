package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView cardNumberTextView, deckTitleView;

    Boolean keyStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_page);

        // Display instruction toast
        Toast.makeText(this, "Click on the card to flip", Toast.LENGTH_LONG).show();

        // Retrieve deck data from previous activity
        Intent intent = getIntent();
        deckData deck = intent.getParcelableExtra("deck");
        Map<String, String> dictionary = deck.wordDict;
        Log.d("deck", dictionary.toString());
        String selectedLanguage = getIntent().getStringExtra("selected_language");

        deckTitleView = findViewById(R.id.deckTitle);
        flipButton = findViewById(R.id.flipBtn);
        cardNumberTextView = findViewById(R.id.cardNumber);

        // Set deck title
        deckTitleView.setText(deck.name);

        // Initialize current index and index size
        currentIndex = 0;
        Set<String> keys = dictionary.keySet();
        List<String> keyList = new ArrayList<>(keys);
        indexSize = keys.size();

        // Display initial card
        updateCardNumber();
        flipButton.setText(keyList.get(0));

        // Flip button click listener
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FlipButton", "flipped - " + currentIndex + " " + indexSize);
                // Toggle between word and translation on flip
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

        // Back button click listener
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LearnActivity
                Intent intent = new Intent(FlipCardActivity.this, LearnActivity.class);
                intent.putExtra("selected_language", selectedLanguage);
                startActivity(intent);
            }
        });

        // Navigate back to LearnActivity
        nextButton = findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FlipButton", "next clicked - " + currentIndex + " " + currentText);
                // Move to the next card
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
                updateCardNumber();
            }
        });

        // Previous button click listener
        prevButton = findViewById(R.id.previousBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FlipButton", "previous clicked - " + currentIndex + " " + currentText);
                // Move to the previous card
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
                updateCardNumber();
            }
        });
    }

    // Update card number display
    private void updateCardNumber() {
        String cardNumberText = (currentIndex + 1) + "/" + indexSize;
        cardNumberTextView.setText(cardNumberText);
    }
}
