package com.ironz.binaryprefs.events;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import com.ironz.binaryprefs.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main thread preference change listener bridge
 */
@SuppressWarnings("unused")
public final class MainThreadEventBridgeImpl implements EventBridge {

    private static final Map<String, List<OnSharedPreferenceChangeListener>> allListeners = new ConcurrentHashMap<>();

    private final Handler handler = new Handler();
    private final List<OnSharedPreferenceChangeListener> listeners;

    public MainThreadEventBridgeImpl(String prefName) {
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
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListenerWrapper listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListenerWrapper listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListenersUpdate(final Preferences preferences, final String key, byte[] bytes) {
        notifyListeners(preferences, key);
    }

    @Override
    public void notifyListenersRemove(Preferences preferences, String key) {
        notifyListeners(preferences, key);
    }

    private void notifyListeners(final Preferences preferences, final String key) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnSharedPreferenceChangeListener listener : listeners) {
                    listener.onSharedPreferenceChanged(preferences, key);
                }
            }
        });
    }
}