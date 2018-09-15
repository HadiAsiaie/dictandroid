package com.vivinte.dictandroid.models

import android.content.SharedPreferences

object UserUtils {

    fun getDefaults(): SharedPreferences {
        return MyApplication.getAppContext().getSharedPreferences("defaults", 0)
    }
}