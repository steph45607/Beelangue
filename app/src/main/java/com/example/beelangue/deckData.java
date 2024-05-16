package com.example.beelangue;

import java.util.ArrayList;

public class deckData {
    public String name;
    public ArrayList<String> words;
//    public Integer length = words.size();

    public deckData(){}

    public deckData(String name, ArrayList<String> words) {
        this.name = name;
        this.words = words;
    }
}
