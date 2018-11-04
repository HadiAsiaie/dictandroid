package com.vivinte.dictandroid.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "word_table")
public class Word {
    @PrimaryKey
    @ColumnInfo(name = "word")
    @NonNull
    private String mWord;


    public Word(@NonNull String word) {this.mWord = word;}

    @NonNull
    public String getWord(){return this.mWord;}
}
