package com.ironz.binaryprefs;

import android.util.Pair;

import com.ironz.binaryprefs.cache.CacheHashProvider;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.*;
import java.util.concurrent.locks.Lock;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final EventBridge eventsBridge;
    private final CacheHashProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final Lock readLock;
    private final Lock writeLock;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(String prefName,
                             FileAdapter fileAdapter,
                             ExceptionHandler exceptionHandler,
                             EventBridge eventsBridge,
                             CacheHashProvider cacheProvider,
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
        fetchCache();
    }

    private void fetchCache() {
        readLock.lock();
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
            readLock.unlock();
        }
    }

    @Override
    public Map<String, Object> getAll() {
        readLock.lock();
        try {
            Map<String, Object> all = cacheProvider.getAll();
            HashMap<String, Object> copy = new HashMap<>(all.size());
            for (String key : all.keySet()) {
                Object value = all.get(key);
                Object redefinedValue = serializerFactory.redefineMutable(value);
                copy.put(key, redefinedValue);
            }
            return Collections.unmodifiableMap(copy);
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

                Set<String> result = (Set<String>) cacheProvider.get(key);

                if (fileAdapter.contains(key)) {
                    int hash = cacheProvider.getHash(key);

                    if (result.hashCode() != hash) {
                        result = (Set<String>) serializerFactory.deserialize(key, fileAdapter.fetch(key));
                    }

                }

                return new HashSet<>(result);
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
    @SuppressWarnings("unchecked")
    public Collection<String> getCollectionString(String key, Collection<String> defValue) {
        readLock.lock();
        try {
            if (cacheProvider.contains(key)) {


                Collection<String> result = (Collection<String>) cacheProvider.get(key);

                if (fileAdapter.contains(key)) {
                    int hash = cacheProvider.getHash(key);

                    if (result.hashCode() != hash) {
                        result = (Collection<String>) serializerFactory.deserialize(key, fileAdapter.fetch(key));
                    }

                }

                return result;
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
                    writeLock
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