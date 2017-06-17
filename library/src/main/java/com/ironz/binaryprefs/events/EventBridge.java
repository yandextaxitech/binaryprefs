package com.ironz.binaryprefs.events;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.ironz.binaryprefs.Preferences;

/**
 * Describes contract for preferences change events
 */
public interface EventBridge {

    /**
     * Behaves exactly like in
     * {@link android.content.SharedPreferences#registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener)}
     * method.
     *
     * @param listener listener
     */
    void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);

    /**
     * Behaves exactly like in
     * {@link android.content.SharedPreferences#unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener)}}
     * method.
     *
     * @param listener listener
     */
    void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);

    /**
     * Notifies all listeners which has been subscribed on preferences changes about preference update
     *
     * @param preferences preferences, might be null (IPC)
     * @param key         target key
     * @param value       target value for cache update
     */
    void notifyListenersUpdate(Preferences preferences, String key, Object value);

    /**
     * Notifies all listeners which has been subscribed on preferences changes about preference remove
     *
     * @param preferences preferences, might be null (ipc)
     * @param key         target key
     */
    void notifyListenersRemove(Preferences preferences, String key);
}