package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.PreferenceEventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.util.Bits;

import java.io.Externalizable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final PreferenceEventBridge eventsBridge;
    private final CacheProvider cacheProvider;

    private final Class lock = BinaryPreferences.class;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter,
                             ExceptionHandler exceptionHandler,
                             PreferenceEventBridge eventsBridge,
                             CacheProvider cacheProvider) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.eventsBridge = eventsBridge;
        this.cacheProvider = cacheProvider;
        defineCache();
    }

    private void defineCache() {
        for (String name : fileAdapter.names()) {
            try {
                byte[] bytes = fileAdapter.fetch(name);
                cacheProvider.put(name, bytes);
            } catch (Exception ignore) {
                //don't care, just ignore
            }
        }
    }

    @Override
    public Map<String, ?> getAll() {
        synchronized (lock) {
            try {
                return getAllInternal();
            } catch (Exception e) {
                exceptionHandler.handle(e, "getAll method");
            }
            return new HashMap<>();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (lock) {
            try {
                return getStringInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        synchronized (lock) {
            try {
                return getStringSetInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValues;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (lock) {
            try {
                return getIntInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (lock) {
            try {
                return getLongInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (lock) {
            try {
                return getFloatInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (lock) {
            try {
                return getBooleanInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public <T extends Externalizable> T getObject(Class<T> clazz, String key, T defValue) {
        synchronized (lock) {
            try {
                return getObjectInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public boolean contains(String key) {
        synchronized (lock) {
            return containsInternal(key);
        }
    }

    @Override
    public PreferencesEditor edit() {
        synchronized (lock) {
            return new BinaryPreferencesEditor(this, fileAdapter, exceptionHandler, eventsBridge, cacheProvider, lock);
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (lock) {
            eventsBridge.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (lock) {
            eventsBridge.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    private Map<String, ?> getAllInternal() {
        Map<String, Object> map = new HashMap<>();
        for (String key : cacheProvider.keys()) {
            byte[] bytes = cacheProvider.get(key);
            map.put(key, Bits.tryDeserializeByFlag(bytes));
        }
        return map;
    }

    private String getStringInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.stringFromBytesWithFlag(bytes);
    }

    private Set<String> getStringSetInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.stringSetFromBytesWithFlag(bytes);
    }

    private int getIntInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.intFromBytesWithFlag(bytes);
    }

    private long getLongInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.longFromBytesWithFlag(bytes);
    }

    private float getFloatInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.floatFromBytesWithFlag(bytes);
    }

    private boolean getBooleanInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.booleanFromBytesWithFlag(bytes);
    }

    private <T extends Externalizable> T getObjectInternal(String key) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private boolean containsInternal(String key) {
        return cacheProvider.contains(key);
    }
}