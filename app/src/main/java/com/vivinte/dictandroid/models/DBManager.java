package com.vivinte.dictandroid.models;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DBManager extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "final_fa.db";
    private static final int DATABASE_VERSION = 1;
    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}
