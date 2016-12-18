package com.ironz.binaryprefs.cache;


import java.util.Set;

/**
 * Adapter abstraction which describes a cache contract.
 * It's used for making unique implementation for concrete cache operations.
 */
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
     * @param key    unique key
     * @param values values
     */
    // TODO: 12/12/16 implement a few variants for storing a multiple values into one file table
    void putStringSet(String key, Set<String> values);

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
    void putBoolean(String key, boolean value);

    /**
     * Returns {@link java.lang.String} value from cache by specified key.
     * If cache table has not have value by key the "defValue" param will be returned instead.
     * If value have inconsistent type exception will be thrown.
     *
     * @param key      unique key
     * @param defValue def value
     * @return value from cache or "defValue" param
     * @throws ClassCastException if value have inconsistent type
     */
    String getString(String key, String defValue);

    /**
     * Returns {@link java.util.Set} of {@link java.lang.String} values from cache by specified key.
     * If cache table has not have value by key the "defValues" param will be returned instead.
     * If value have inconsistent type exception will be thrown.
     *
     * @param key       unique key
     * @param defValues def values
     * @return value from cache or "defValues" param
     * @throws ClassCastException if value have inconsistent type
     */
    Set<String> getStringSet(String key, Set<String> defValues);

    /**
     * Returns {@link java.lang.Integer} primitive value from cache by specified key.
     * If cache table has not have value by key the "defValue" param will be returned instead.
     * If value have inconsistent type exception will be thrown.
     *
     * @param key      unique key
     * @param defValue def value
     * @return value from cache or "defValue" param
     * @throws ClassCastException if value have inconsistent type
     */
    int getInt(String key, int defValue);

    /**
     * Returns {@link java.lang.Long} primitive value from cache by specified key.
     * If cache table has not have value by key the "defValue" param will be returned instead.
     * If value have inconsistent type exception will be thrown.
     *
     * @param key      unique key
     * @param defValue def value
     * @return value from cache or "defValue" param
     * @throws ClassCastException if value have inconsistent type
     */
    long getLong(String key, long defValue);

    /**
     * Returns {@link java.lang.Float} primitive value from cache by specified key.
     * If cache table has not have value by key the "defValue" param will be returned instead.
     * If value have inconsistent type exception will be thrown.
     *
     * @param key      unique key
     * @param defValue def value
     * @return value from cache or "defValue" param
     * @throws ClassCastException if value have inconsistent type
     */
    float getFloat(String key, float defValue);

    /**
     * Returns {@link java.lang.Boolean} primitive value from cache by specified key.
     * If cache table has not have value by key the "defValue" param will be returned instead.
     * If value have inconsistent type exception will be thrown.
     *
     * @param key      unique key
     * @param defValue def value
     * @return value from cache or "defValue" param
     * @throws ClassCastException if value have inconsistent type
     */
    boolean getBoolean(String key, boolean defValue);
}
