package com.ironz.binaryprefs.events;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.ironz.binaryprefs.Preferences;
import com.ironz.binaryprefs.cache.CacheProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple preference change listener bridge
 */
public final class SimpleEventBridgeImpl implements EventBridge {

    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();
    private final CacheProvider cacheProvider;

    public SimpleEventBridgeImpl(CacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListenersUpdate(Preferences preferences, String key, Object value) {
        cacheProvider.put(key, value);
        notifyListeners(preferences, key);
    }

    @Override
    public void notifyListenersRemove(Preferences preferences, String key) {
        cacheProvider.remove(key);
        notifyListeners(preferences, key);
    }

    private void notifyListeners(Preferences preferences, String key) {
        for (OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(preferences, key);
        }
    }
}