package com.example.beelangue;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Parcelable class representing a deck of flashcards
public class deckData implements Parcelable {
    // properties
    public String name;
    public List<String> words;
    public Map<String, String> wordDict;

    public deckData(){}

    // Parameterized constructor
    public deckData(String name, ArrayList<String> words, Map<String, String> wordDict) {
        this.wordDict = wordDict != null  ? wordDict : new HashMap<>();
        this.name = name;
        this.words = words;
    }

    // Parcelable constructor
    protected deckData(Parcel in) {
        name = in.readString();
        words = in.createStringArrayList();
        wordDict = (HashMap<String, String>) in.readHashMap(String.class.getClassLoader());
    }

    // Parcelable.Creator instance to create instances of deckData class from a Parcel
    public static final Creator<deckData> CREATOR = new Creator<deckData>() {
        @Override
        public deckData createFromParcel(Parcel in) {
            return new deckData(in);
        }

        @Override
        public deckData[] newArray(int size) {
            return new deckData[size];
        }
    };

    // Describe the kinds of special objects contained in this Parcelable instance's marshaled representation
    @Override
    public int describeContents() {
        return 0;
    }

    // Write the object's data to the passed-in Parcel
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(words);
        dest.writeMap(wordDict);
    }
}
