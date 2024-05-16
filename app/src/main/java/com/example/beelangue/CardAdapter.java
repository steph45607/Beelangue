package com.example.beelangue;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CardAdapter extends ArrayAdapter<cardData> {
    TextView countryText, idText;
//    View view;

    public CardAdapter(Activity context, ArrayList<cardData> source) {
        super(context,0,source);
    }
    public View getView(int position, View convertView, ViewGroup parent){
//        View listItemView = convertView;
//        if(listItemView == null) {
//            listItemView = LayoutInflater.from(getContext()).inflate(
//                    R.layout.card_deck, parent, false);
//        }
//        cardData currentCard = getItem(position);
//        countryText = listItemView.findViewById(R.id.countryName);
//        assert currentCard != null;
//        countryText.setText(currentCard.country);
//        idText = listItemView.findViewById(R.id.countryID);
//        idText.setText(currentCard.id);
//        return listItemView;

        cardData card = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_deck, parent, false);
        }
        countryText = convertView.findViewById(R.id.countryName);
        assert card != null;
        countryText.setText(card.country);
        idText = convertView.findViewById(R.id.countryID);
        idText.setText(card.id);
        return convertView;
    }
}
