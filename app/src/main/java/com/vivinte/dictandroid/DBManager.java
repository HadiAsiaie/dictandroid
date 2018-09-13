package com.vivinte.dictandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBManager extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "final_fa.db";
    private static final int DATABASE_VERSION = 1;

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}
