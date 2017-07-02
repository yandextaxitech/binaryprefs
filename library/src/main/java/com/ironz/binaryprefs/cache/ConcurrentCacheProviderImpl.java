package com.ironz.binaryprefs.cache;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent cache provider which locks on concrete key.
 */
@SuppressWarnings("unused")
public final class ConcurrentCacheProviderImpl implements CacheProvider {

    private static final Map<String, Map<String, Object>> caches = new ConcurrentHashMap<>();

    private final Map<String, Object> currentCache;

    public ConcurrentCacheProviderImpl(String name) {
        currentCache = initCaches(name);
    }

    private Map<String, Object> initCaches(String name) {
        if (caches.containsKey(name)) {
            return caches.get(name);
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        caches.put(name, map);
        return map;
    }

    @Override
    public boolean contains(String key) {
        return currentCache.containsKey(key);
    }

    @Override
    public void put(String key, Object value) {
        currentCache.put(key, value);
    }

    @Override
    public String[] keys() {
        return currentCache.keySet().toArray(new String[0]);
    }

    @Override
    public Object get(String key) {
        return currentCache.get(key);
    }

    @Override
    public void remove(String name) {
        currentCache.remove(name);
    }

    @Override
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(currentCache);
    }
}