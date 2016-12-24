package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.cache.CacheAdapter;
import com.ironz.binaryprefs.files.FileAdapter;

import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements SharedPreferences {

    private final CacheAdapter cacheAdapter;
    private final FileAdapter fileAdapter;

    public BinaryPreferences(CacheAdapter cacheAdapter, FileAdapter fileAdapter) {
        this.cacheAdapter = cacheAdapter;
        this.fileAdapter = fileAdapter;
    }

    @Override
    public Map<String, ?> getAll() {
        return cacheAdapter.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        return cacheAdapter.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return cacheAdapter.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return cacheAdapter.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return cacheAdapter.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return cacheAdapter.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return cacheAdapter.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return cacheAdapter.contains(key);
    }

    @Override
    public Editor edit() {
        return new BinaryPreferencesEditor(cacheAdapter, fileAdapter);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
