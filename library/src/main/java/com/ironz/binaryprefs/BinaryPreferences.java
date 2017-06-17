package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final EventBridge eventsBridge;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final Object lock;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(String prefName,
                             FileAdapter fileAdapter,
                             ExceptionHandler exceptionHandler,
                             EventBridge eventsBridge,
                             CacheProvider cacheProvider,
                             TaskExecutor taskExecutor,
                             SerializerFactory serializerFactory,
                             LockFactory lockFactory) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.eventsBridge = eventsBridge;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        this.lock = lockFactory.get(prefName);
        fetchCache();
    }

    private void fetchCache() {
        synchronized (lock) {
            for (String name : fileAdapter.names()) {
                try {
                    byte[] bytes = fileAdapter.fetch(name);
                    Object o = serializerFactory.deserialize(name, bytes);
                    cacheProvider.put(name, o);
                } catch (Exception e) {
                    exceptionHandler.handle(name, e);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getAll() {
        synchronized (lock) {
            return cacheProvider.getAll();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (String) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String key, Set<String> defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                Set<String> strings = (Set<String>) cacheProvider.get(key);
                return new HashSet<>(strings);
            }
            return defValue;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (int) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (long) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (float) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (boolean) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Persistable> T getPersistable(String key, T defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                T t = (T) cacheProvider.get(key);
                return (T) t.deepCopy();
            }
            return defValue;
        }
    }

    @Override
    public byte getByte(String key, byte defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (byte) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public short getShort(String key, short defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (short) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public char getChar(String key, char defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (char) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public double getDouble(String key, double defValue) {
        synchronized (lock) {
            if (cacheProvider.contains(key)) {
                return (double) cacheProvider.get(key);
            }
            return defValue;
        }
    }

    @Override
    public boolean contains(String key) {
        synchronized (lock) {
            return cacheProvider.contains(key);
        }
    }

    @Override
    public PreferencesEditor edit() {
        synchronized (lock) {
            return new BinaryPreferencesEditor(
                    this,
                    fileAdapter,
                    exceptionHandler,
                    eventsBridge,
                    taskExecutor,
                    serializerFactory,
                    cacheProvider,
                    lock
            );
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
}