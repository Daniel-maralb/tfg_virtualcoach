package com.example.virtualcoach.app.preferences;

import android.content.SharedPreferences;

public abstract class Preference<T> {
    abstract void set(SharedPreferences.Editor editor, T value);

    abstract T get(SharedPreferences preferences);

    public final String key;
    public final T defaultValue;

    Preference(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    void remove(SharedPreferences.Editor editor) {
        editor.remove(key);
    }
}