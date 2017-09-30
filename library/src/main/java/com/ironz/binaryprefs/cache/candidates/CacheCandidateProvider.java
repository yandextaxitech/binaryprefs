package com.ironz.binaryprefs.cache.candidates;

import java.util.Set;

/**
 * Describes contract which store, fetch and remove cache candidates.
 * Cache candidates - this is a key names which is candidates for file fetching.
 */
public interface CacheCandidateProvider {
    /**
     * Returns true if value is exists false otherwise
     *
     * @param key target key
     * @return exists condition
     */
    boolean contains(String key);

    /**
     * Puts file to cache, value not might be null
     *
     * @param key target key
     */
    void put(String key);

    /**
     * Returns all keys inside cache
     *
     * @return keys array
     */
    Set<String> keys();

    /**
     * Removes specific value from cache by given key
     *
     * @param key target key for remove
     */
    void remove(String key);
}