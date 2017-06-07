package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.serialization.persistable.Persistable;

/**
 * Extension of {@link SharedPreferences} class for using plain serialization mechanism
 */
public interface Preferences extends SharedPreferences {

    @Override
    PreferencesEditor edit();

    /**
     * Retrieve an object value from the preferences.
     *
     * @param clazz    required class type
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    <T extends Persistable> T getPersistable(Class<T> clazz, String key, T defValue);
}