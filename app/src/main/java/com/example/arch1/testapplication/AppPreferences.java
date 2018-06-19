package com.example.arch1.testapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private SharedPreferences sharedPreferences;
    private Context ctx;
    private SharedPreferences.Editor editor;

    final private String SHARED_PREF_STRING = "com.example.arch1.testapplication";
    final static String APP_THEME = "appTheme";
    final static String APP_FIRST_LAUNCH = "AppFirstLaunch";
    final static String APP_ANSWER_PRECISION = "precision";
    final static String APP_ANGLE = "angle";
    final static String APP_HISTORY = "history";

    public AppPreferences(Context context) {
        ctx = context;
        sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_STRING,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getStringPreference(String key) {
        return sharedPreferences.getString(key,"");
    }

    public void setStringPreference(String key, String value) {
        editor.putString(key,value);
        editor.commit();
    }

    public static AppPreferences getInstance(Context context) {
        return new AppPreferences(context);
    }

    public boolean getBooleanPreference(String key) {
        return sharedPreferences.getBoolean(key,true);
    }

    public void setBooleanPreference(String key, Boolean value) {
        editor.putBoolean(key,value);
        editor.commit();
    }

}
