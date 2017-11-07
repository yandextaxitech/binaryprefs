package com.ironz.binaryprefs.event;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Wrapped {@link OnSharedPreferenceChangeListener} class which holds current preferences instance for delivering
 * correct {@link com.ironz.binaryprefs.Preferences} instance to 2'st argument in
 * {@link OnSharedPreferenceChangeListener#onSharedPreferenceChanged(SharedPreferences, String)} method.
 * Please do not use this class inside your codebase because main propose for this implementation is holding
 * strong reference to preferences instance.
 */
public final class OnSharedPreferenceChangeListenerWrapper implements OnSharedPreferenceChangeListener {

    private final SharedPreferences currentPreferences;
    private final OnSharedPreferenceChangeListener listener;

    public OnSharedPreferenceChangeListenerWrapper(SharedPreferences currentPreferences, OnSharedPreferenceChangeListener listener) {
        this.currentPreferences = currentPreferences;
        this.listener = listener;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences unused, String key) {
        listener.onSharedPreferenceChanged(currentPreferences, key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OnSharedPreferenceChangeListenerWrapper that = (OnSharedPreferenceChangeListenerWrapper) o;

        if (listener != null ? !listener.equals(that.listener) : that.listener != null) return false;
        return currentPreferences != null ? currentPreferences.equals(that.currentPreferences) : that.currentPreferences == null;
    }

    @Override
    public int hashCode() {
        int result = listener != null ? listener.hashCode() : 0;
        result = 31 * result + (currentPreferences != null ? currentPreferences.hashCode() : 0);
        return result;
    }
}