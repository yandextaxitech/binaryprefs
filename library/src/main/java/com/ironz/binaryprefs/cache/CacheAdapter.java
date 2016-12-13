package com.ironz.binaryprefs.cache;


import java.util.Set;

/**
 * Adapter abstraction which describes a cache contract.
 * It's used for making unique implementation for concrete cache operations.
 */
// TODO: 12/12/16 check SharedPreferences allows null or not
public interface CacheAdapter {
    /**
     * Adds {@link java.lang.String} defValue by specified key.
     * If cache implementation already has the defValue by this key it will be overwritten.
     *
     * @param key      unique key
     * @param defValue default value
     */
    void putString(String key, String defValue);

    /**
     * Adds {@link java.util.Set} of {@link java.lang.String} values by specified key.
     * If cache implementation already has the defValue by this key it will be overwritten.
     *
     * @param key      unique key
     * @param defValue def value
     */
    // TODO: 12/12/16 implement a few designs for storing a multiple values into one file table
    void putStringSet(String key, Set<String> defValue);

    /**
     * Adds {@link java.lang.Integer} primitive defValue by specified key.
     * If cache implementation already has the defValue by this key it will be overwritten.
     *
     * @param key      unique key
     * @param defValue def value
     */
    void putInt(String key, int defValue);

    /**
     * Adds {@link java.lang.Long} primitive defValue by specified key.
     * If cache implementation already has the defValue by this key it will be overwritten.
     *
     * @param key      unique key
     * @param defValue def value
     */
    void putLong(String key, long defValue);

    /**
     * Adds {@link java.lang.Float} primitive defValue by specified key.
     * If cache implementation already has the defValue by this key it will be overwritten.
     *
     * @param key      unique key
     * @param defValue def value
     */
    void putFloat(String key, float defValue);

    /**
     * Adds {@link java.lang.Boolean} primitive defValue by specified key.
     * If cache implementation already has the defValue by this key it will be overwritten.
     *
     * @param key      unique key
     * @param defValue def value
     */
    void putBoolean(String key, boolean defValue);
}
