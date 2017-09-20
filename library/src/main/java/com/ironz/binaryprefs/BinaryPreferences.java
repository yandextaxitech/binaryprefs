package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.event.EventBridge;
import com.ironz.binaryprefs.event.OnSharedPreferenceChangeListenerWrapper;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.task.FutureBarrier;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.*;
import java.util.concurrent.locks.Lock;

final class BinaryPreferences implements Preferences {

    private final FileTransaction fileTransaction;
    private final EventBridge eventsBridge;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final Lock readLock;
    private final Lock writeLock;

    BinaryPreferences(FileTransaction fileTransaction,
                      EventBridge eventsBridge,
                      CacheProvider cacheProvider,
                      TaskExecutor taskExecutor,
                      SerializerFactory serializerFactory,
                      LockFactory lockFactory) {
        this.fileTransaction = fileTransaction;
        this.eventsBridge = eventsBridge;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        this.readLock = lockFactory.getReadLock();
        this.writeLock = lockFactory.getWriteLock();
        fetchCache();
    }

    private void fetchCache() {
        readLock.lock();
        try {
            FutureBarrier barrier = taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    fetchCacheInternal();
                }
            });
            barrier.completeBlockingUnsafe();
        } finally {
            readLock.unlock();
        }
    }

    private void fetchCacheInternal() {
        if (!cacheProvider.keys().isEmpty()) {
            return;
        }
        for (TransactionElement element : fileTransaction.fetchAll()) {
            String name = element.getName();
            byte[] bytes = element.getContent();
            Object o = serializerFactory.deserialize(name, bytes);
            cacheProvider.put(name, o);
        }
    }

    @Override
    public Map<String, Object> getAll() {
        readLock.lock();
        try {
            Map<String, Object> all = cacheProvider.getAll();
            Map<String, Object> clone = new HashMap<>(all.size());
            for (String key : all.keySet()) {
                Object value = all.get(key);
                Object redefinedValue = serializerFactory.redefinePersistable(value);
                clone.put(key, redefinedValue);
            }
            return Collections.unmodifiableMap(clone);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (String) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            //noinspection unchecked
            Set<String> strings = (Set<String>) o;
            return new HashSet<>(strings);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (int) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (long) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (float) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (boolean) o;
        } finally {
            readLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Persistable> T getPersistable(String key, T defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (T) ((T) o).deepClone();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public byte getByte(String key, byte defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (byte) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public short getShort(String key, short defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (short) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public char getChar(String key, char defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (char) o;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public double getDouble(String key, double defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return (double) o;
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
    public void putString(String key, String value) {
        PreferencesEditor editor = editInternal();
        writeLock.lock();
        try {
            editor.putString(key, value)
                    .commit();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putStringSet(String key, Set<String> values) {
        PreferencesEditor editor = editInternal();
        writeLock.lock();
        try {
            editor.putStringSet(key, values)
                    .commit();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putInt(String key, int value) {
        PreferencesEditor editor = editInternal();
        writeLock.lock();
        try {
            editor.putInt(key, value)
                    .commit();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putLong(String key, long value) {
        PreferencesEditor editor = editInternal();
        writeLock.lock();
        try {
            editor.putLong(key, value)
                    .commit();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putFloat(String key, float value) {
        PreferencesEditor editor = editInternal();
        writeLock.lock();
        try {
            editor.putFloat(key, value)
                    .commit();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putBoolean(String key, boolean value) {
        PreferencesEditor editor = editInternal();
        writeLock.lock();
        try {
            editor.putBoolean(key, value)
                    .commit();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor edit() {
        return editInternal();
    }

    private PreferencesEditor editInternal() {
        readLock.lock();
        try {
            return new BinaryPreferencesEditor(
                    fileTransaction,
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
    public Set<String> keys() {
        readLock.lock();
        try {
            return cacheProvider.keys();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        writeLock.lock();
        try {
            OnSharedPreferenceChangeListenerWrapper wrapper = new OnSharedPreferenceChangeListenerWrapper(this, listener);
            eventsBridge.registerOnSharedPreferenceChangeListener(wrapper);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        writeLock.lock();
        try {
            OnSharedPreferenceChangeListenerWrapper wrapper = new OnSharedPreferenceChangeListenerWrapper(this, listener);
            eventsBridge.unregisterOnSharedPreferenceChangeListener(wrapper);
        } finally {
            writeLock.unlock();
        }
    }
}