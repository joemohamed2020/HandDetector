package com.example.imagepro;

import android.content.Context;
import android.content.SharedPreferences;

class SessionManager {
    private static final String SHARED_PREF_NAME = "session";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedInEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getLoggedInEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
