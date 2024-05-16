package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LearnActivity extends AppCompatActivity {

    ImageButton back, create;
    MaterialButton create1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_page);

        back = findViewById(R.id.backBtn);
        create = findViewById(R.id.createBtn);
        create1 = findViewById(R.id.createDeck);

        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LearnActivity.this, LearnExploreActivity.class);
                        startActivity(i);
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
    }
}
