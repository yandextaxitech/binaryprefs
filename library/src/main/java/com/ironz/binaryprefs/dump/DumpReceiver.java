package com.ironz.binaryprefs.dump;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ironz.binaryprefs.Preferences;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class DumpReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "pref_name";
    private static final String PREF_KEY = "pref_key";

    private static final Map<String, Preferences> BINARY_PREFERENCES_HASH_MAP = new ConcurrentHashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        String prefName = intent.getStringExtra(PREF_NAME);

        if (!BINARY_PREFERENCES_HASH_MAP.containsKey(prefName)) {
            Log.e(DumpReceiver.class.getName(), String.format("Cannot find '%s' preference for dumping!", prefName));
            return;
        }

        Preferences preferences = BINARY_PREFERENCES_HASH_MAP.get(prefName);
        Map<String, ?> all = preferences.getAll();

        if (intent.hasExtra(PREF_KEY)) {
            String key = intent.getStringExtra(PREF_KEY);
            Log.d(DumpReceiver.class.getName(), key + ": " + all.get(key) + "\n");
            return;
        }

        for (String key : all.keySet()) {
            Object o = all.get(key);
            Log.d(DumpReceiver.class.getName(), key + ": " + o + "\n");
        }
    }

    public static void register(String name, Preferences preferences) {
        BINARY_PREFERENCES_HASH_MAP.put(name, preferences);
    }

    public static void unregister(String name) {
        BINARY_PREFERENCES_HASH_MAP.remove(name);
    }
}