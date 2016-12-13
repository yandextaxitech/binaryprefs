package com.ironz.binaryprefs.cache;


import java.util.Set;

/**
 * Adapter abstraction which describes a cache contract.
 * It's used for making unique implementation for concrete cache operations.
 */
// TODO: 12/12/16 check SharedPreferences allows null or not
public interface CacheAdapter {
    /**
     * Adds {@link java.lang.String} value by specified key.
     * If cache implementation already has the value by this key it will be overwritten.
     *
     * @param key   unique key
     * @param value value
     */
    void putString(String key, String value);

    /**
     * Adds {@link java.util.Set} of {@link java.lang.String} values by specified key.
     * If cache implementation already has the value by this key it will be overwritten.
     *
     * @param key   unique key
     * @param value value
     */
    // TODO: 12/12/16 implement a few designs for storing a multiple values into one file table
    void putStringSet(String key, Set<String> value);

    /**
     * Adds {@link java.lang.Integer} primitive value by specified key.
     * If cache implementation already has the value by this key it will be overwritten.
     *
     * @param key   unique key
     * @param value value
     */
    void putInt(String key, int value);

    /**
     * Adds {@link java.lang.Long} primitive value by specified key.
     * If cache implementation already has the value by this key it will be overwritten.
     *
     * @param key   unique key
     * @param value value
     */
    void putLong(String key, long value);

    /**
     * Adds {@link java.lang.Float} primitive value by specified key.
     * If cache implementation already has the value by this key it will be overwritten.
     *
     * @param key   unique key
     * @param value value
     */
    void putFloat(String key, float value);

    /**
     * Adds {@link java.lang.Boolean} primitive value by specified key.
     * If cache implementation already has the value by this key it will be overwritten.
     *
     * @param key   unique key
     * @param value value
     */
    void putBoolean(String key, Boolean value);
}
