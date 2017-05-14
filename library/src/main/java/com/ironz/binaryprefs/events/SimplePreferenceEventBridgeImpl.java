package com.ironz.binaryprefs.events;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.ironz.binaryprefs.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple preference change listener bridge
 */
public final class SimplePreferenceEventBridgeImpl implements PreferenceEventBridge {

    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListeners(Preferences preferences, String key) {
        for (SharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(preferences, key);
        }
    }
}