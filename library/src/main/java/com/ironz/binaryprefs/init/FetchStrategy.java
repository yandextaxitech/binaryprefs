package com.ironz.binaryprefs.init;

/**
 * Interface which describes contract for object fetching
 */
public interface FetchStrategy {
    /**
     * Returns value for target key
     *
     * @param key      given key for fetching
     * @param defValue default value if target value does not exists
     * @return actual value if exists, default value otherwise
     */
    Object getValue(String key, Object defValue);
}