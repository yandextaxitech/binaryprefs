package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

import java.util.Map;
import java.util.Set;

/**
 * Extension of {@link SharedPreferences} class for using plain serialization mechanism
 */
public interface Preferences extends SharedPreferences {

    /**
     * Create a new Editor for these preferences, through which you can make
     * modifications to the data in the preferences and atomically commit those
     * changes back to the SharedPreferences object.
     * <p>
     * <p>Note that you <em>must</em> call {@link Editor#commit()} to have any
     * changes you perform in the Editor actually show up in the
     * SharedPreferences.
     * <p>
     * Also note that if you trying to call {@link PreferencesEditor#commit()} or
     * {@link PreferencesEditor#apply()} methods for one instance of
     * {@link PreferencesEditor} twice - {@link com.ironz.binaryprefs.exception.TransactionInvalidatedException}
     * will be thrown.
     *
     * @return Returns a new instance of the {@link Editor} interface, allowing
     * you to modify the values in this SharedPreferences object.
     */
    @Override
    PreferencesEditor edit();

    /**
     * Retrieve all values from the preferences.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences
     * @deprecated Please use {@link #keys()} method to iterate all values.
     * This method dramatically decreases performance because performs full map
     * recreation and all values inside by immutability reasons.
     */
    @Override
    @Deprecated
    Map<String, ?> getAll();

    /**
     * Retrieve all keys set for values which exists in current preferences set.
     *
     * @return a set containing all set of key representing the preferences
     */
    Set<String> keys();

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
     * Retrieve an @{code byte[]} value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    byte[] getByteArray(String key, byte[] defValue);
}