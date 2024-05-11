package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button profileBtn;
    ImageButton flagBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void getLanguages(){
        Log.d("confirm", "running i am running");
//        String name = db.collection("language").get().toString();
//        Log.d("langID", name);
        db.collection("language")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
//                                String documentID = doc.getID
                                Log.d("koesmanto", doc.getId() + " =>" + doc.getData());
                            }
                        }
//                        else{
//                            Log.w("koesmanto", "Error getting documents", task.getException());
//                        }
//                    }

//                    @Override
//                    public void onSuccess(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot doc: task.getResult()){
//                                Log.d("koesmanto", doc.getId() + " =>" + doc.getData());
//                            }
//                        }
//                        else{
//                            Log.w("koesmanto", "Error getting documents", task.getException());
//                        }
//                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("koesmanto", "error bla bla", e);
                    }
                })
        ;
        Log.d("staniswinata", "ended");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        getLanguages();

        profileBtn = findViewById(R.id.profile);
        profileBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(i);
                    }
                }
        );

        flagBtn = findViewById(R.id.japanese);
        flagBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, LearnExploreActivity.class);
                        startActivity(i);
                    }
                }
        );
    }


}