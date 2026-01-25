package com.example.tbokhle;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "tbokhle_prefs";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";

    // ADDED by maria
    private static final String KEY_HOUSEHOLD_ID = "household_id"; // ← ADDED

    private final SharedPreferences sp;

    public SessionManager(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(String userId, String email) {
        sp.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    //  ADDED by maria
    public void setHouseholdId(int householdId) { // ← ADDED
        sp.edit().putInt(KEY_HOUSEHOLD_ID, householdId).apply();
    }

    // ADDED by maria
    public int getHouseholdId() { // ← ADDED
        return sp.getInt(KEY_HOUSEHOLD_ID, -1);
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

