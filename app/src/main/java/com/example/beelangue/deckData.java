package com.example.beelangue;

import java.util.ArrayList;
import java.util.Map;

public class deckData {
    public String name;
    public ArrayList<String> words;
    Map<String, String> wordDict;
//    public Integer length = words.size();

    public deckData(){}

    public deckData(String name, ArrayList<String> words) {
        this.name = name;
        this.words = words;
    }
    public void setWordDict(Map<String, String> list){
        this.wordDict = list;
    }
}
