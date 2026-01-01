package com.example.tbokhle;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // One preferences file for the whole app
    private static final String PREF_NAME = "tbokhle_prefs";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences sp;

    public SessionManager(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(String userId, String email) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sp.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return sp.getString(KEY_USER_ID, "");
    }

    public String getEmail() {
        return sp.getString(KEY_EMAIL, "");
    }

    public void logout() {
        sp.edit().clear().apply();
    }
}
