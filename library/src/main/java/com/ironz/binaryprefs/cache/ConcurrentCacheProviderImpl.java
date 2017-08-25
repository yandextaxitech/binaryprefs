package com.ironz.binaryprefs.cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent cache provider which locks on concrete key.
 */
public final class ConcurrentCacheProviderImpl implements CacheProvider {

    private static final String[] EMPTY_ARRAY = new String[0];

    private final Map<String, Object> currentCache;

    private final Set<String> candidates = new HashSet<>();

    public ConcurrentCacheProviderImpl(String prefName, Map<String, Map<String, Object>> allCaches) {
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
    public String[] keys() {
        Set<String> keySet = currentCache.keySet();
        return keySet.toArray(EMPTY_ARRAY);
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
        return currentCache;
    }

    @Override
    public Set<String> candidates() {
        return candidates;
    }

    @Override
    public void putCandidate(String name) {
        candidates.add(name);
    }
}