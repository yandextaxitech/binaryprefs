package com.ironz.binaryprefs.event;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple preference change listener bridge. Uses current thread for delivering all events.
 */
public final class SimpleEventBridgeImpl implements EventBridge {

    private static final Map<String, List<OnSharedPreferenceChangeListener>> allListeners = new ConcurrentHashMap<>();

    private final List<OnSharedPreferenceChangeListener> listeners;

    public SimpleEventBridgeImpl(String prefName) {
        this.listeners = initListeners(prefName);
    }

    private List<OnSharedPreferenceChangeListener> initListeners(String prefName) {
        if (allListeners.containsKey(prefName)) {
            return allListeners.get(prefName);
        }
        List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();
        allListeners.put(prefName, listeners);
        return listeners;
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
    public void notifyListenersUpdate(String key, byte[] bytes) {
        notifyListeners(key);
    }

    @Override
    public void notifyListenersRemove(String key) {
        notifyListeners(key);
    }

    private void notifyListeners(String key) {
        for (OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(null, key);
        }
    }
}