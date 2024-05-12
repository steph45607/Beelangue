package com.example.beelangue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button profileBtn;
    ImageButton flagBtn;

//    TextView language;

//    private FirebaseFirestore firestore;


//    public void getLanguages(){
//        Log.d("confirm", "running i am running");
////        String name = db.collection("language").get().toString();
////        Log.d("langID", name);
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
////                        else{
////                            Log.w("koesmanto", "Error getting documents", task.getException());
////                        }
////                    }
//
////                    @Override
////                    public void onSuccess(@NonNull Task<QuerySnapshot> task) {
////                        if(task.isSuccessful()){
////                            for(QueryDocumentSnapshot doc: task.getResult()){
////                                Log.d("koesmanto", doc.getId() + " =>" + doc.getData());
////                            }
////                        }
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
//        getLanguages();

//        firestore = FirebaseFirestore.getInstance();
//        new Thread(){
//            public void run(){
//                loadData();
//            }
//        }.start();

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

//    private void loadData(){
//        language.findViewById(R.id.label);
//        language.setText("test passed");

//        firestore.collection("japanese")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        Log.d("koesmanto", "for loop");
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("koesmanto", document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d("koesmanto", "Error getting documents: ", task.getException());
//                        }
//                        Log.d("koesmanto", "end for loop");
//                    }
//                });
//    }

}

