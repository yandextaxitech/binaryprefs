package com.ironz.binaryprefs.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class ConcurrentCacheProviderImpl implements CacheProvider {

    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    @Override
    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void put(String key, byte[] value) {
        cache.put(key, value);
    }

    @Override
    public String[] keys() {
        return cache.keySet().toArray(new String[0]);
    }

    @Override
    public byte[] get(String key) {
        return cache.get(key);
    }

    @Override
    public void remove(String name) {
        cache.remove(name);
    }
}