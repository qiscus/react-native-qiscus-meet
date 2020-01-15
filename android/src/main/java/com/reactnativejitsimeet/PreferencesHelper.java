package com.reactnativejitsimeet;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static PreferencesHelper INSTANCE;
    private SharedPreferences sharedPreferences;

    private PreferencesHelper(Context context) {
        sharedPreferences = context
                .getApplicationContext()
                .getSharedPreferences("meet.qiscus.lib", Context.MODE_PRIVATE);
    }

    public static PreferencesHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PreferencesHelper(context);
        }
        return INSTANCE;
    }

    public SharedPreferences Pref() {
        return sharedPreferences;
    }


    public Boolean getConference() {
        return sharedPreferences.getBoolean("isConference", true);
    }

    public void setConference(boolean isConfrence) {
        sharedPreferences.edit().putBoolean("isConference", isConfrence).apply();
    }

    public void setBaseUrl(String isBaseUrl) {
        sharedPreferences.edit().putString("isBaseUrl", isBaseUrl).apply();
    }

    public String getBaseUrl() {
        return sharedPreferences.getString("isBaseUrl", "");
    }
}
