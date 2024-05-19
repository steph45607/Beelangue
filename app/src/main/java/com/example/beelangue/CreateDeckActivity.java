package com.example.beelangue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CreateDeckActivity extends AppCompatActivity {

    Button createDeckBtn, addBtn;
    ImageButton backButton;
    EditText titleText, wordText;
    ListView wordsListView;
    ArrayList<String> words;
    ArrayAdapter<String> wordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_deck);

        // Retrieve selected language from previous activity
        String selectedLanguage = getIntent().getStringExtra("selected_language");

        wordText = findViewById(R.id.wordText);
        titleText = findViewById(R.id.deckTitle);
        wordsListView = findViewById(R.id.wordsListView);
        words = new ArrayList<>();
        wordsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words);
        wordsListView.setAdapter(wordsAdapter);

        // Set up the add button to add words to the list
        addBtn = findViewById(R.id.addCardBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWordToList();
            }
        });

        // Set up the back button to navigate back to LearnActivity
        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(CreateDeckActivity.this, LearnActivity.class);
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                    }
                }
        );

        // Set up the create deck button to finalize and save the deck
        createDeckBtn = findViewById(R.id.finishDeckBtn);
        createDeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("deckDB", "button clicked");
                checkName(selectedLanguage);
            }
        });

        // Set up long click listener for list items to delete words
        wordsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String word = words.get(position);
            deleteConfirmation(position, word);
            return true;
        });
    }

    // Method to add a word to the list
    private void addWordToList() {
        String word = wordText.getText().toString().trim();
        if (!word.isEmpty()) {
            if (!words.contains(word)) {
                words.add(word);
                wordsAdapter.notifyDataSetChanged();
                wordText.setText("");
                Log.d("deckDB", "word: " + word);
            } else {
                Toast.makeText(this, "word already in the list", Toast.LENGTH_SHORT).show();
                wordText.setText("");
            }
        } else {
            Toast.makeText(this, "Please enter a word", Toast.LENGTH_SHORT).show();
        }

    }

    // Method to show a confirmation dialog before deleting a word
    private void deleteConfirmation(int position, String word) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Word")
                .setMessage("Are you sure you want to delete the word \"" + word + "\"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWord(position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Method to delete a word from the list
    private void deleteWord(int position) {
        String word = words.remove(position);
        wordsAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Word \"" + word + "\" deleted", Toast.LENGTH_SHORT).show();
        Log.d("deckDB", "word deleted: " + word);
    }

    // Method to delete a word from the list
    private void checkName(String selectedLanguage) {
        String name = titleText.getText().toString().trim().toLowerCase();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a deck title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (words.isEmpty()) {
            Toast.makeText(this, "Please add at least one word", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a deck with the same name already exists in the database
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("deck");
        Query query = ref.orderByChild("name").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(CreateDeckActivity.this, "Deck name already exists.", Toast.LENGTH_SHORT).show();
                    Log.d("deckDB", "deck name already exists: " + name);
                } else {
                    saveDeckToDatabase(selectedLanguage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("deckDB", "Error checking deck name: " + databaseError.getMessage());
                Toast.makeText(CreateDeckActivity.this, "Error checking deck name. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to save the deck to the database
    private void saveDeckToDatabase(String selectedLanguage) {
        Log.d("deckDB", "save button clicked");
        String title;
        title = titleText.getText().toString().toLowerCase();
        Log.d("deckDB", title + " " + words.get(0));
        deckData deck = new deckData(title, words, null);
        Log.d("deckDB", "deck object created, not saved");

        // Reference to the database
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("deck");
        Log.d("deckDB", "database reference defined");

        // Save the deck to the database
        ref.push().setValue(deck)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("deckDB", "Data added to database");
                        Toast.makeText(getApplicationContext(), "Deck created successfully", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(CreateDeckActivity.this, LearnActivity.class);
                        i.putExtra("selected_language", selectedLanguage);
                        startActivity(i);
                        finish();
                    }
                });
    }
}
