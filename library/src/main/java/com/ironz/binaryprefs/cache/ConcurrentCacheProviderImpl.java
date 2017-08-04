package com.ironz.binaryprefs.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent cache provider which locks on concrete key.
 */
public final class ConcurrentCacheProviderImpl implements CacheProvider {

    private final Map<String, Object> currentCache;

    public ConcurrentCacheProviderImpl(String prefName, Map<String, Map<String, Object>> allCaches) {
        currentCache = initCaches(prefName, allCaches);
    }

    private Map<String, Object> initCaches(String prefName, Map<String, Map<String, Object>> allCaches) {
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
    public String[] keys() {
        Set<String> keySet = currentCache.keySet();
        return keySet.toArray(new String[0]);
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