package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.global.GlobalLockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final EventBridge eventsBridge;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final Lock readLock;
    private final Lock writeLock;
    private final GlobalLockFactory globalLockFactory;

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
        this.readLock = lockFactory.getReadLock(prefName);
        this.writeLock = lockFactory.getWriteLock(prefName);
        this.globalLockFactory = lockFactory.getGlobalLockFactory();
        fetchCache();
    }

    private void fetchCache() {
        readLock.lock();
        Lock lock = globalLockFactory.getLock();
        lock.lock();
        Map<String, Object> map = new HashMap<>();
        try {
            for (String name : fileAdapter.names()) {
                byte[] bytes = fileAdapter.fetch(name);
                Object o = serializerFactory.deserialize(name, bytes);
                map.put(name, o);
            }
            for (String name : map.keySet()) {
                cacheProvider.put(name, map.get(name));
            }
        } finally {
            lock.unlock();
            readLock.unlock();
        }
    }

    @Override
    public Map<String, Object> getAll() {
        readLock.lock();
        try {
            return cacheProvider.getAll();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (String) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String key, Set<String> defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                Set<String> strings = (Set<String>) cacheProvider.get(key);
                return new HashSet<>(strings);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (int) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (long) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (float) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (boolean) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Persistable> T getPersistable(String key, T defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                T t = (T) cacheProvider.get(key);
                return (T) t.deepCopy();
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public byte getByte(String key, byte defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (byte) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public short getShort(String key, short defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (short) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public char getChar(String key, char defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (char) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public double getDouble(String key, double defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {
                return (double) cacheProvider.get(key);
            }
            return defValue;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(String key) {
        readLock.lock();
        try {
            return cacheProvider.contains(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public PreferencesEditor edit() {
        readLock.lock();
        try {
            return new BinaryPreferencesEditor(
                    this,
                    fileAdapter,
                    exceptionHandler,
                    eventsBridge,
                    taskExecutor,
                    serializerFactory,
                    cacheProvider,
                    writeLock,
                    globalLockFactory
            );

        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        writeLock.lock();
        try {
            eventsBridge.registerOnSharedPreferenceChangeListener(listener);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        writeLock.lock();
        try {
            eventsBridge.unregisterOnSharedPreferenceChangeListener(listener);
        } finally {
            writeLock.unlock();
        }
    }
}