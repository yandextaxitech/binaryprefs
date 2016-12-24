package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.cache.CacheAdapter;
import com.ironz.binaryprefs.files.FileAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class BinaryPreferencesEditor implements SharedPreferences.Editor {

    private final Map<String, Object> commitMap = new HashMap<>();

    private final CacheAdapter cacheAdapter;
    private final FileAdapter fileAdapter;

    BinaryPreferencesEditor(CacheAdapter cacheAdapter, FileAdapter fileAdapter) {
        this.cacheAdapter = cacheAdapter;
        this.fileAdapter = fileAdapter;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
        return this;
    }

    @Override
    public boolean commit() {
        return false;
    }

    @Override
    public void apply() {

    }
}
