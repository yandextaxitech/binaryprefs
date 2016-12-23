package com.ironz.binaryprefs.cache;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Concrete cache adapter which implements cache operations
 */
// TODO: 13/12/16 implement tests
@SuppressWarnings("unchecked")
public class CacheAdapterImpl implements CacheAdapter {

    //we are define explicit HashMap type because invokevirtual faster than invokeinterface
    private final HashMap<String, Object> cache = new HashMap<>();

    @Override
    public void putString(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void putStringSet(String key, Set<String> values) {
        cache.put(key, values);
    }

    @Override
    public void putInt(String key, int value) {
        cache.put(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        cache.put(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        cache.put(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        cache.put(key, value);
    }

    @Override
    public String getString(String key, String defValue) {
        if (cache.containsKey(key)) {
            return (String) cache.get(key);
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (cache.containsKey(key)) {
            return (Set<String>) cache.get(key);
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (cache.containsKey(key)) {
            return (int) cache.get(key);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (cache.containsKey(key)) {
            return (long) cache.get(key);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (cache.containsKey(key)) {
            return (float) cache.get(key);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (cache.containsKey(key)) {
            return (boolean) cache.get(key);
        }
        return defValue;
    }

    @Override
    public Map<String, ?> getAll() {
        return cache;
    }
}
