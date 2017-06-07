package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.serialization.Bits;
import com.ironz.binaryprefs.serialization.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final EventBridge eventsBridge;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;

    private final Class lock = BinaryPreferences.class;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter,
                             ExceptionHandler exceptionHandler,
                             EventBridge eventsBridge,
                             CacheProvider cacheProvider,
                             TaskExecutor taskExecutor) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.eventsBridge = eventsBridge;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        defineCache();
    }

    private void defineCache() {
        synchronized (lock) {
            for (String name : fileAdapter.names()) {
                try {
                    byte[] bytes = fileAdapter.fetch(name);
                    cacheProvider.put(name, bytes);
                } catch (Exception e) {
                    exceptionHandler.handle(e, name);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getAll() {
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
    public <T extends Persistable> T getPersistable(Class<T> clazz, String key, T defValue) {
        synchronized (lock) {
            try {
                return getPersistableInternal(key, clazz);
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
            return new BinaryPreferencesEditor(this, fileAdapter, exceptionHandler, eventsBridge, taskExecutor, lock);
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

    private Map<String, Object> getAllInternal() {
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

    private <T extends Persistable> T getPersistableInternal(String key, Class<T> clazz) {
        byte[] bytes = cacheProvider.get(key);
        return Bits.persistableFromBytes(bytes, clazz);
    }

    private boolean containsInternal(String key) {
        return cacheProvider.contains(key);
    }
}