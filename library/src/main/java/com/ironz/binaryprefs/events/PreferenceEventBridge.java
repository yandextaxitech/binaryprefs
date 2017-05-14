package com.ironz.binaryprefs.events;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.ironz.binaryprefs.Preferences;

/**
 * Describes contract for preferences change events
 */
public interface PreferenceEventBridge {

    /**
     * Behavior exact like in
     * {@link android.content.SharedPreferences#registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener)}
     * method.
     *
     * @param listener listener
     */
    void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);

    /**
     * Behavior exact like in
     * {@link android.content.SharedPreferences#unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener)}}
     * method.
     *
     * @param listener listener
     */
    void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);

    /**
     * Notifies all listeners which has been subscribed on preferences changes
     *
     * @param preferences might be null (ipc)
     * @param key         target key
     */
    void notifyListeners(Preferences preferences, String key);
}