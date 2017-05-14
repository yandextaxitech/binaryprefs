package com.ironz.binaryprefs.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.ironz.binaryprefs.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses global broadcast receiver mechanism for delivering all key change events.
 * Main propose for using this implementation is IPC mechanism.
 */
@SuppressWarnings("unused")
public final class BroadcastPreferenceEventBridgeImpl implements PreferenceEventBridge {

    private static final String ACTION_PREFERENCE_CHANGED = "com.ironz.binaryprefs.ACTION_PREFERENCE_CHANGED";
    private static final String PREFERENCE_CHANGED_KEY = "preference_changed_key";

    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();
    private final IntentFilter filter = new IntentFilter(ACTION_PREFERENCE_CHANGED);
    private final Context context;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ACTION_PREFERENCE_CHANGED.equals(intent.getAction())) {
                return;
            }
            for (OnSharedPreferenceChangeListener listener : listeners) {
                listener.onSharedPreferenceChanged(null, intent.getStringExtra(PREFERENCE_CHANGED_KEY));
            }
        }
    };

    public BroadcastPreferenceEventBridgeImpl(Context context) {
        this.context = context;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (listeners.isEmpty()) {
            context.registerReceiver(receiver, filter);
        }
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            context.unregisterReceiver(receiver);
        }
    }

    @Override
    public void notifyListeners(Preferences preferences, String key) {
        Intent intent = new Intent(ACTION_PREFERENCE_CHANGED);
        intent.putExtra(PREFERENCE_CHANGED_KEY, key);
        context.sendBroadcast(intent);
    }
}