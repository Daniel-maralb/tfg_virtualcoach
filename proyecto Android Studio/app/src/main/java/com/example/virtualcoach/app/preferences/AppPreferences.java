package com.example.virtualcoach.app.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class AppPreferences {
    public static final StringPreference CURRENT_USER_ID = new StringPreference("current_user_id", null);
    public static final StringPreference DB_TO_IMPORT = new StringPreference("db_to_import", "/storage/emulated/0/GBExports/db.sqlite3");
    public static final StringPreference LAST_IMPORT = new StringPreference("last_import_datetime", null);

    private final SharedPreferences preferences;

    @Inject
    AppPreferences(@ApplicationContext Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressLint("ApplySharedPref")
    public <T> void set(Preference<T> preference, T value) {
        SharedPreferences.Editor editor = preferences.edit();
        preference.set(editor, value);

        editor.commit();
    }

    public <T> T get(Preference<T> preference) {
        return preference.get(preferences);
    }

    @SuppressLint("ApplySharedPref")
    public <T> void remove(Preference<T> preference) {
        SharedPreferences.Editor editor = preferences.edit();
        preference.remove(editor);

        editor.commit();
    }
}
