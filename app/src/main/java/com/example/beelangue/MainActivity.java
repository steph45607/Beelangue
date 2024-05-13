package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button profileBtn;
    ImageButton flagBtn;

    TextView language;

//    FirebaseFirestore firestore;


//    public void getLanguages(){
//        language = findViewById(R.id.japanese);
//        Log.d("confirm", "running i am running");
//        Log.d("check firebase", db.toString());
//        db.collection("language")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        language.setText(task.toString());
////                        if (task.isSuccessful()) {
////                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                Log.d("koesmanto", document.toString()))
////                            }
////                        }
//        }});
//    };
//        String name = db.collection("language").get().toString();
//        Log.d("langID", name);
//        firestore.collection("language")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
////                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
////                                String documentID = doc.getID
//                                Log.d("koesmanto", doc.getId() + " =>" + doc.getData());
//                            }
//                        }
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
////                        else{
////                            Log.w("koesmanto", "Error getting documents", task.getException());
////                        }
////                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("koesmanto", "error bla bla", e);
//                    }
//                })
//        ;
//        Log.d("staniswinata", "ended");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        language = findViewById(R.id.label);
        Log.d("koesmanto", "there");

//        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");
//        ref.setValue("test");
//        Log.d("koesmanto", "here");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String key = dataSnapshot.child("indonesian/country").getValue(String.class);
//                String value = dataSnapshot.getValue(String.class);
                Log.d("koesmanto", "Value is: " + key);
                language.setText(key);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("koesmanto", "Failed to read value.", error.toException());
            }
        });



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

    private void loadData(){
        language.findViewById(R.id.label);
        language.setText("test passed");
        Log.d("koesmanto", "there");

//        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://beelangue-d5b83-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("language");
//        ref.setValue("test");
//        Log.d("koesmanto", "here");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer key = dataSnapshot.child("japanese/id").getValue(Integer.class);
//                String value = dataSnapshot.getValue(String.class);
                Log.d("koesmanto", "Value is: " + key);
                language.setText(key);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("koesmanto", "Failed to read value.", error.toException());
            }
        });

    }

}

