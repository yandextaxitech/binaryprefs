package com.ironz.binaryprefs.cache.provider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent cache provider which locks on concrete key.
 */
public final class ConcurrentCacheProvider implements CacheProvider {

    private final Map<String, Object> currentCache;

    public ConcurrentCacheProvider(String prefName, Map<String, Map<String, Object>> allCaches) {
        this.currentCache = putIfAbsentCache(prefName, allCaches);
    }

    private Map<String, Object> putIfAbsentCache(String prefName, Map<String, Map<String, Object>> allCaches) {
        if (allCaches.containsKey(prefName)) {
            return allCaches.get(prefName);
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        allCaches.put(prefName, map);
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
    public Set<String> keys() {
        Set<String> s = currentCache.keySet();
        return Collections.unmodifiableSet(s);
    }

    @Override
    public Object get(String key) {
        return currentCache.get(key);
    }

    @Override
    public void remove(String key) {
        currentCache.remove(key);
    }

    @Override
    public Map<String, Object> getAll() {
        return currentCache;
    }
}