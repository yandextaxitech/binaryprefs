package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

import java.util.Set;

/**
 * Interface used for modifying values in a {@link Preferences}
 * object.  All changes you make in an editor are batched, and not copied
 * back to the original {@link Preferences} until you call {@link #commit}
 * or {@link #apply}
 */
public interface PreferencesEditor extends SharedPreferences.Editor {

    @Override
    PreferencesEditor putString(String key, String value);

    @Override
    PreferencesEditor putStringSet(String key, Set<String> values);

    @Override
    PreferencesEditor putInt(String key, int value);

    @Override
    PreferencesEditor putLong(String key, long value);

    @Override
    PreferencesEditor putFloat(String key, float value);

    @Override
    PreferencesEditor putBoolean(String key, boolean value);

    @Override
    PreferencesEditor remove(String key);

    @Override
    PreferencesEditor clear();

    /**
     * Set an Object value in the preferences editor, to be written back once
     * {@link #commit} or {@link #apply} are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.  Passing {@code null}
     *              for this argument is equivalent to calling {@link #remove(String)} with
     *              this key.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    <T extends Persistable> PreferencesEditor putPersistable(String key, T value);

    /**
     * Set an byte value in the preferences editor, to be written back once
     * {@link #commit} or {@link #apply} are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    PreferencesEditor putByte(String key, byte value);

    /**
     * Set an short value in the preferences editor, to be written back once
     * {@link #commit} or {@link #apply} are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    PreferencesEditor putShort(String key, short value);

    /**
     * Set an char value in the preferences editor, to be written back once
     * {@link #commit} or {@link #apply} are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    PreferencesEditor putChar(String key, char value);

    /**
     * Set an double value in the preferences editor, to be written back once
     * {@link #commit} or {@link #apply} are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    PreferencesEditor putDouble(String key, double value);

    /**
     * Set an @{code byte} value in the preferences editor, to be written back once
     * {@link #commit} or {@link #apply} are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    PreferencesEditor putByteArray(String key, byte[] value);
}