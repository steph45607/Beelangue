package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LearnActivity extends AppCompatActivity {

    ImageButton back, searchBtn;
    MaterialButton create1;
    LinearLayout buttonContainer;
    EditText searchEditText;
    private DatabaseReference databaseReference;
    private ValueEventListener deckEventListener;
    private ArrayList<deckData> decks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_page);

        back = findViewById(R.id.backBtn);
//        create = findViewById(R.id.createBtn);
        create1 = findViewById(R.id.createDeck);
        buttonContainer = findViewById(R.id.buttonContainer);

        searchBtn = findViewById(R.id.searchBtn);
        searchEditText = findViewById(R.id.searchEditText);
        decks = new ArrayList<deckData>();

        String selectedLanguage = getIntent().getStringExtra("selected_language");

        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                        finish();
                    }
                }
        );

        create1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnActivity.this, CreateDeckActivity.class);
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                    }
                }
        );

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEditText.getVisibility() == View.GONE) {
                    searchEditText.setVisibility(View.VISIBLE);
                } else {
                    searchEditText.setVisibility(View.GONE);
                    searchEditText.setText("");
                    searchDecks("", selectedLanguage);
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("koesmanto", s.toString());
                searchDecks(s.toString(), selectedLanguage);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("deck");
        fetchDecks(selectedLanguage);
    }

    private void fetchDecks(String language) {
        Log.d("koesmanto", "fetch deck called");

        deckEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("koesmanto", "deck gotten called");
                buttonContainer.removeAllViews();
                decks.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String deckName = snapshot.child("name").getValue(String.class);
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };
                    ArrayList<String> wordList = snapshot.child("words").getValue(t);
                    deckData deck = new deckData(deckName, wordList, null);
                    Log.d("koesmanto", deckName);
                    decks.add(0, deck);
                }
                for (deckData deck : decks) {
                    createDeckButton(deck, language);  // Populate buttons in the reversed order
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("koesmanto", databaseError.getMessage());
                Toast.makeText(LearnActivity.this, "Failed to load decks.", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(deckEventListener);
    }

    private void createDeckButton(final deckData deck, String language) {
        Button button = new Button(this);
        button.setText(deck.name);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("koesmanto", deck.words.toString());
                Map<String, String> dict = new HashMap<>();
//                List<String> dict = new ArrayList<>();
                for (String word : deck.words) {
                    translate(word, language, new TranslationCallback() {
                        @Override
                        public void onTranslationCompleted(String translatedText) {
                            if (translatedText != null) {
//                                dict.add(translatedText);
                                dict.put(word, translatedText);
                                Log.d("koesmanto", "Translated text = " + translatedText);
                            }
                            if (dict.size() == deck.words.size()) {
                                deckData newDeck = new deckData(deck.name, (ArrayList<String>) deck.words, dict);
                                Log.d("koesmanto", "Dict is set: " + newDeck.wordDict);
                                // Proceed to the next activity after all translations are done
                                Intent i = new Intent(LearnActivity.this, FlipCardActivity.class);
                                i.putExtra("selected_language", language);
                                i.putExtra("deck", newDeck);
                                startActivity(i);
                            }
                        }
                    });
                }
            }
        });
        buttonContainer.addView(button);
    }

    private void searchDecks(String query, String language) {
        buttonContainer.removeAllViews();
        Log.d("koesmanto", "decknames" + decks.toString());
        for (deckData deck : decks) {
            Log.d("koesmanto", "deckname: " + deck.name);
            if (deck.name.toLowerCase().contains(query.toLowerCase())) {
                createDeckButton(deck, language);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // to avoid the fetching happening in the bg when the activity is not opened
        if (deckEventListener != null) {
            databaseReference.removeEventListener(deckEventListener);
        }
    }

    public interface TranslationCallback {
        void onTranslationCompleted(String translatedText);
    }


    private void translate(String word, String targetLanguage, TranslationCallback callback) {
        String targetLanguageCode;
        try {
            Field field = TranslateLanguage.class.getField(targetLanguage.toUpperCase());
            targetLanguageCode = (String) field.get(null);
        } catch (Exception e) {
            targetLanguageCode = TranslateLanguage.INDONESIAN; // Default to Indonesian if not found
        }

        assert targetLanguageCode != null;
        TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguageCode)
                .build();

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi() // Optional: Require WiFi for download
                .build();

        Translator translator = Translation.getClient(translatorOptions);

        translator.translate(word)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        Log.d("translate", String.format("%s (%s)", word, translatedText));
                        callback.onTranslationCompleted(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ObjectDetection", "Translation Failed: " + exception.getMessage());
                        callback.onTranslationCompleted(null);
                    }
                });
    }
}
