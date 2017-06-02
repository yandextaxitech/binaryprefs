package com.ironz.binaryprefs.cache;

/**
 * Describes contract which store, fetch and remove cached elements
 */
public interface CacheProvider {

    /**
     * Returns true if value is exists false otherwise
     *
     * @param key target key
     * @return exists condition
     */
    boolean contains(String key);

    /**
     * Puts file to cache, value might be null
     *
     * @param key   target key
     * @param value target value
     */
    void put(String key, byte[] value);

    /**
     * Returns all keys inside cache
     *
     * @return keys array
     */
    String[] keys();

    /**
     * Returns value by specific key or null if value not exist
     *
     * @param key target key for fetching
     * @return value or null if not exist
     */
    byte[] get(String key);

    /**
     * Removes specific value from cache by given key
     *
     * @param name target key for remove
     */
    void remove(String name);
}