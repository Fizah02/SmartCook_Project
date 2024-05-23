package com.example.smartcook;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_LOGGED_IN = "loggedIn";

    SharedPreferencesHelper() {
        // Private constructor to prevent instantiation
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn() {
        Context context = null;
        return getSharedPreferences(context).getBoolean(KEY_LOGGED_IN, false);
    }

    public static void setLoggedIn(boolean loggedIn) {
        Context context = null;
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_LOGGED_IN, loggedIn);
        editor.apply();
    }
}
