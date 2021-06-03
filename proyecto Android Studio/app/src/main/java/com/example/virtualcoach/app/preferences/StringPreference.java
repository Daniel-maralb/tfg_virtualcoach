package com.example.virtualcoach.app.preferences;

import android.content.SharedPreferences;

public class StringPreference extends Preference<String> {
    StringPreference(String key, String defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public void set(SharedPreferences.Editor editor, String value) {
        editor.putString(key, value);
    }

    @Override
    public String get(SharedPreferences preferences) {
        return preferences.getString(key, defaultValue);
    }
}
