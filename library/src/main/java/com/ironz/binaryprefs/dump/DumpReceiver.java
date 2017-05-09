package com.ironz.binaryprefs.dump;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ironz.binaryprefs.BinaryPreferences;

import java.util.HashMap;
import java.util.Map;

public final class DumpReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "pref_name";
    private static final Map<String, BinaryPreferences> BINARY_PREFERENCES_HASH_MAP = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        String prefName = intent.getStringExtra(PREF_NAME);

        if (!BINARY_PREFERENCES_HASH_MAP.containsKey(prefName)) {
            throw new NullPointerException("Cannot find '%s' preference for dumping!");
        }

        BinaryPreferences preferences = BINARY_PREFERENCES_HASH_MAP.get(prefName);
        Map<String, ?> all = preferences.getAll();
        Log.d(DumpReceiver.class.getName(), all.toString());
    }

    @SuppressWarnings("unused")
    public void appendPreference(String name, BinaryPreferences preferences) {
        BINARY_PREFERENCES_HASH_MAP.put(name, preferences);
    }
}