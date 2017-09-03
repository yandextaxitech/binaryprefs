package com.ironz.binaryprefs.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Concurrent cache provider which locks on concrete key.
 */
public final class ConcurrentCacheProvider implements CacheProvider {

    private final Map<String, Object> currentCache;
    private final Set<String> candidates;

    public ConcurrentCacheProvider(String prefName,
                                   Map<String, Map<String, Object>> allCaches,
                                   Map<String, Set<String>> allCandidates) {
        this.currentCache = putIfAbsentCache(prefName, allCaches);
        this.candidates = putIfAbsentCandidates(prefName, allCandidates);
    }

    private Map<String, Object> putIfAbsentCache(String prefName, Map<String, Map<String, Object>> allCaches) {
        if (allCaches.containsKey(prefName)) {
            return allCaches.get(prefName);
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        allCaches.put(prefName, map);
        return map;
    }

    private Set<String> putIfAbsentCandidates(String prefName, Map<String, Set<String>> allCandidates) {
        if (allCandidates.containsKey(prefName)) {
            return allCandidates.get(prefName);
        }
        ConcurrentSkipListSet<String> set = new ConcurrentSkipListSet<>();
        allCandidates.put(prefName, set);
        return set;
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

    @Override
    public Set<String> candidates() {
        return candidates;
    }

    @Override
    public boolean containsCandidate(String key) {
        return candidates.contains(key);
    }

    @Override
    public void putCandidate(String key) {
        candidates.add(key);
    }
}