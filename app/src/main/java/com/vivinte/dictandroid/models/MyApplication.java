package com.vivinte.dictandroid.models;


import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {
    private static Context context;
    private static RequestQueue queue;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        DBUtils.INSTANCE.createDB(context);
        queue =Volley.newRequestQueue(context);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
    public static RequestQueue getQueue() {
        return MyApplication.queue;
    }
}
