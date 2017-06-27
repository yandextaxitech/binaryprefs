package com.ironz.binaryprefs.events;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import com.ironz.binaryprefs.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Main thread preference change listener bridge
 */
@SuppressWarnings("unused")
public final class MainThreadEventBridgeImpl implements EventBridge {

    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();
    private final Handler handler = new Handler();

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
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