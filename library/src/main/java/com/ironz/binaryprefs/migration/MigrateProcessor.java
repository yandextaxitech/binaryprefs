package com.ironz.binaryprefs.migration;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.ironz.binaryprefs.Preferences;
import com.ironz.binaryprefs.PreferencesEditor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Migrates all {@link SharedPreferences} list into {@link Preferences}.
 */
public final class MigrateProcessor {

    private final Set<SharedPreferences> migrate = new HashSet<>();

    public void add(SharedPreferences preferences) {
        migrate.add(preferences);
    }

    public void migrateTo(Preferences preferences) {
        for (SharedPreferences sharedPreferences : migrate) {
            applyOne(sharedPreferences, preferences);
        }
    }

    @SuppressLint("ApplySharedPref")
    private void applyOne(SharedPreferences from, Preferences to) {

        Map<String, ?> all = from.getAll();

        if (all.isEmpty()) {
            return;
        }

        PreferencesEditor editor = to.edit();

        for (String key : all.keySet()) {
            migrateOneValue(all, editor, key);
        }
        boolean commit = editor.commit();

        if (commit) {
            from.edit().clear().commit();
        }
    }

    private void migrateOneValue(Map<String, ?> all, PreferencesEditor editor, String key) {
        Object value = all.get(key);
        if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        if (value instanceof Set) {
            //noinspection unchecked
            editor.putStringSet(key, (Set<String>) value);
        }
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
    }
}