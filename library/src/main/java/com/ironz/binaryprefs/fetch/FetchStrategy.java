package com.ironz.binaryprefs.fetch;

import java.util.Map;

/**
 * Interface which describes contract for object fetching
 */
public interface FetchStrategy {
    /**
     * Returns value for target key.
     *
     * @param key      given key for fetching
     * @param defValue default value if target value does not exists
     * @return actual value if exists, default value otherwise
     */
    Object getValue(String key, Object defValue);

    /**
     * Returns all value from current cache.
     *
     * @return all value from current preferences
     */
    Map<String, Object> getAll();

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     * false otherwise.
     */
    boolean contains(String key);
}