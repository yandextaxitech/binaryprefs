package com.ironz.binaryprefs.cache.candidates;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public final class ConcurrentCacheCandidateProvider implements CacheCandidateProvider {

    private final Set<String> currentCandidates;

    public ConcurrentCacheCandidateProvider(String prefName, Map<String, Set<String>> allCacheCandidates) {
        currentCandidates = putIfAbsentCache(prefName, allCacheCandidates);
    }

    private Set<String> putIfAbsentCache(String prefName, Map<String, Set<String>> allCacheCandidates) {
        if (allCacheCandidates.containsKey(prefName)) {
            return allCacheCandidates.get(prefName);
        }
        Set<String> set = new ConcurrentSkipListSet<>();
        allCacheCandidates.put(prefName, set);
        return set;
    }

    @Override
    public boolean contains(String key) {
        return currentCandidates.contains(key);
    }

    @Override
    public void put(String key) {
        currentCandidates.add(key);
    }

    @Override
    public Set<String> keys() {
        return Collections.unmodifiableSet(currentCandidates);
    }

    @Override
    public void remove(String key) {
        currentCandidates.remove(key);
    }
}