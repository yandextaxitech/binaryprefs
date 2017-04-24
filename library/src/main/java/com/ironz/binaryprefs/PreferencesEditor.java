package com.ironz.binaryprefs;

import android.content.SharedPreferences;

import java.io.Externalizable;

/**
 * Interface used for modifying values in a {@link Preferences}
 * object.  All changes you make in an editor are batched, and not copied
 * back to the original {@link Preferences} until you call {@link #commit}
 * or {@link #apply}
 */
public interface PreferencesEditor extends SharedPreferences.Editor {
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
    <T extends Externalizable> PreferencesEditor putObject(String key, T value);
}