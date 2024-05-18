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
    private ArrayList<deckData> deckNames;

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
        deckNames = new ArrayList<deckData>();

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

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEditText.getVisibility() == View.GONE) {
                    searchEditText.setVisibility(View.VISIBLE);
                } else {
                    searchEditText.setVisibility(View.GONE);
                    searchEditText.setText("");
                    searchDecks("");
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
                searchDecks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("deck");
        fetchDecks();
    }

    private void fetchDecks() {
        Log.d("koesmanto", "fetch deck called");

        deckEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("koesmanto", "deck gotten called");
                buttonContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String deckName = snapshot.child("name").getValue(String.class);
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>(){};
                    ArrayList<String> wordList = snapshot.child("words").getValue(t);
                    deckData deck = new deckData(deckName, wordList, null);
                    Log.d("koesmanto", deckName);
                    deckNames.add(deck);
                    createDeckButton(deck);

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

    private void createDeckButton(final deckData deck) {
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
                Map<String, String> dict = new HashMap<String, String>();
                for(String word : deck.words){
                    String translated = translate(word, "indonesian");
                    dict.put(word,translated);
                    Log.d("koesmanto", translated + " " + word);
                }
//                deck.setWordDict(dict);
                deck.wordDict = dict;
                Log.d("koesmanto", "dict is set" + dict);
//                TODO What the fuck happened here??? it won't run, im just calling to another activity, wtf?
                Intent i = new Intent(LearnActivity.this, FlipCardActivity.class);
                i.putExtra("deck", deck);
                startActivity(i);
            }
        });
        buttonContainer.addView(button);
    }

    private void searchDecks(String query) {
        buttonContainer.removeAllViews();
        Log.d("koesmanto", "decknames"+deckNames.toString());
        for (deckData deck : deckNames) {
            Log.d("koesmanto", "deckname: "+deck.name);
            if (deck.name.toLowerCase().contains(query.toLowerCase())) {
                createDeckButton(deck);
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

    private String translate(String word, String targetLanguage) {
        final String translatedString;
        targetLanguage = targetLanguage != null ? targetLanguage : "indonesian";
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
//                        translatedString.
                        Log.d("translate", String.format("%s (%s)", word, translatedText));
//                        return translatedText;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ObjectDetection", "Translation Failed: " + exception.getMessage());
                    }
                });
//        return translatedText;
    }
}
