package com.ironz.binaryprefs.cache;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent cache provider which locks on concrete key.
 */
@SuppressWarnings("unused")
public final class ConcurrentCacheProviderImpl implements CacheProvider {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public String[] keys() {
        return cache.keySet().toArray(new String[0]);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public void remove(String name) {
        cache.remove(name);
    }

    @Override
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(cache);
    }
}