package com.ironz.binaryprefs.cache;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete cache adapter which implements concurrent cache operations
 */
public class ConcurrentCacheAdapterImpl implements CacheAdapter {

    private final ConcurrentHashMap<String, String> stringCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> stringSetCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> intCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> longCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Float> floatCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> booleanCache = new ConcurrentHashMap<>();

    @Override
    public void putString(String key, String value) {
        stringCache.put(key, value);
    }

    @Override
    public void putStringSet(String key, Set<String> values) {
        stringSetCache.put(key, values);
    }

    @Override
    public void putInt(String key, int value) {
        intCache.put(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        longCache.put(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        floatCache.put(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        booleanCache.put(key, value);
    }

    @Override
    public String getString(String key, String defValue) {
        if (stringCache.containsKey(key)) {
            return stringCache.get(key);
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (stringSetCache.containsKey(key)) {
            return stringSetCache.get(key);
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (intCache.containsKey(key)) {
            return intCache.get(key);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (longCache.containsKey(key)) {
            return longCache.get(key);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (floatCache.containsKey(key)) {
            return floatCache.get(key);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (booleanCache.containsKey(key)) {
            return booleanCache.get(key);
        }
        return defValue;
    }
}
