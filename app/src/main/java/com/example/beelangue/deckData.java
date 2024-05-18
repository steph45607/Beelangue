package com.example.beelangue;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class deckData implements Parcelable {
    public String name;
    public List<String> words;
    Map<String, String> wordDict;
//    public Integer length = words.size();

    public deckData(){}

    public deckData(String name, ArrayList<String> words, Map<String, String> wordDict) {
        this.wordDict = wordDict != null  ? wordDict : new HashMap<>();
        this.name = name;
        this.words = words;
    }

    public void setWordDict(Map<String, String> wordDict) {
        this.wordDict = wordDict;
    }

    protected deckData(Parcel in) {
        name = in.readString();
        words = in.createStringArrayList();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(words);
        dest.writeMap(wordDict);
    }
}
