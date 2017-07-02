package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

import java.util.Collection;
/**
 * Extension of {@link SharedPreferences} class for using plain serialization mechanism
 */
public interface Preferences extends SharedPreferences {

    @Override
    PreferencesEditor edit();

    /**
     * Retrieve an {@link Persistable} value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    <T extends Persistable> T getPersistable(String key, T defValue);

    /**
     * Retrieve an byte value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    byte getByte(String key, byte defValue);

    /**
     * Retrieve an short value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    short getShort(String key, short defValue);

    /**
     * Retrieve an char value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    char getChar(String key, char defValue);

    /**
     * Retrieve an double value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    double getDouble(String key, double defValue);

    /**
     * Retrieve an Collection<String> value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    Collection<String> getCollectionString(String key, Collection<String> defValue);
}