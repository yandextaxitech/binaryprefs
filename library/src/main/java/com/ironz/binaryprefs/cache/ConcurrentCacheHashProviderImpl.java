package com.ironz.binaryprefs.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent cache provider which locks on concrete key.
 */
@SuppressWarnings("unused")
public final class ConcurrentCacheHashProviderImpl extends ConcurrentCacheProviderImpl implements CacheHashProvider{

    private final Map<String, Integer> hash = new ConcurrentHashMap<>();

    @Override
    public void putWithHash(String key, Object value, int hashCode) {
        put(key,value);
        hash.put(key, hashCode);
    }

    @Override
    public int getHash(String key) {
        return hash.get(key);
    }
}