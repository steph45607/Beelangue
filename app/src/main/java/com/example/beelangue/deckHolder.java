package com.example.beelangue;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class deckHolder extends RecyclerView.ViewHolder{
    TextView countryText, idText;
//    View view;

    public deckHolder(@NonNull View itemView) {
        super(itemView);
        countryText = itemView.findViewById(R.id.countryName);
        idText = itemView.findViewById(R.id.countryId);
    }
}
