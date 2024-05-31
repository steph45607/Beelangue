package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button profileBtn;
    ImageButton indo, france, vietnam, spain, italy, russia;
    Button flagBtn;
    ArrayList<cardData> source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        List<TranslatorOptions> languages = new ArrayList<>();
        languages.add(
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.INDONESIAN)
                        .build());
        languages.add(
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.FRENCH)
                        .build());
        languages.add(
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build());
        languages.add(
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.SPANISH)
                        .build());
        languages.add(
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.RUSSIAN)
                        .build());
        languages.add(
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.ITALIAN)
                        .build());

        List<Translator> translators = new ArrayList<>();
        for (TranslatorOptions options : languages) {
            translators.add(Translation.getClient(options));
        }

        // Initialize the toast variable.
        Toast toast = Toast.makeText(this, "Downloading models", Toast.LENGTH_LONG);
        toast.show();
        CountDownTimer timer = getCountDownTimer();

        for (Translator translator : translators) {
//            Toast.makeText(MainActivity.this, "Models are being downloaded", Toast.LENGTH_LONG).show();
            translator.downloadModelIfNeeded()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("translate", translator.toString()+ " Models downloaded successfully");
                            timer.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("translate", "Model download failed");
                            timer.cancel();
                        }
                    });
        }

        // initialize firebase database reference
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");

        // add listener to read data from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // initiliaze data source list
                source = new ArrayList<>();

                // iterate through the data snapshot to extract country and language information
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

        // define profile button and set click listener
        profileBtn = findViewById(R.id.profile);
        profileBtn.setOnClickListener(
                v -> {
                    // navigate to ProfileActivity when profile button is clicked
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);
                }
        );

        // Initialize and set click listener for Indonesian flag
        indo = findViewById(R.id.indonesiaFlag);
        indo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "indonesia");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for French flag
        france = findViewById(R.id.franceFlag);
        france.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "french");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Vietnamese flag
        vietnam = findViewById(R.id.vietnamFlag);
        vietnam.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "vietnamese");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Spanish flag
        spain = findViewById(R.id.spainFlag);
        spain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "spanish");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Italian flag
        italy = findViewById(R.id.italyFlag);
        italy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "italian");
                        startActivity(i);
                    }
                }
        );

        // Initialize and set click listener for Russian flag
        russia = findViewById(R.id.russiaFlag);
        russia.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        i.putExtra("selected_language", "russian");
                        startActivity(i);
                    }
                }
        );
    }

    @NonNull
    private CountDownTimer getCountDownTimer() {
        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(MainActivity.this,"Downloading models... (" + millisUntilFinished / 1000 + " seconds remaining)",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Model download finished", Toast.LENGTH_SHORT).show();
            }
        };

        timer.start();
        return timer;
    }
}

