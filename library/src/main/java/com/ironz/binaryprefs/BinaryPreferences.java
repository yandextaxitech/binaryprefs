package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
import com.ironz.binaryprefs.event.EventBridge;
import com.ironz.binaryprefs.event.OnSharedPreferenceChangeListenerWrapper;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.fetch.FetchStrategy;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

final class BinaryPreferences implements Preferences {

    private final FileTransaction fileTransaction;
    private final EventBridge eventsBridge;
    private final CacheCandidateProvider cacheCandidateProvider;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final Lock readLock;
    private final Lock writeLock;
    private final FetchStrategy fetchStrategy;

    BinaryPreferences(FileTransaction fileTransaction,
                      EventBridge eventsBridge,
                      CacheCandidateProvider cacheCandidateProvider,
                      CacheProvider cacheProvider,
                      TaskExecutor taskExecutor,
                      SerializerFactory serializerFactory,
                      LockFactory lockFactory,
                      FetchStrategy fetchStrategy) {
        this.fileTransaction = fileTransaction;
        this.eventsBridge = eventsBridge;
        this.cacheCandidateProvider = cacheCandidateProvider;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        this.readLock = lockFactory.getReadLock();
        this.writeLock = lockFactory.getWriteLock();
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    public Map<String, Object> getAll() {
        return fetchStrategy.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        return (String) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        //noinspection unchecked
        return (Set<String>) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return (int) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return (long) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (float) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (boolean) fetchStrategy.getValue(key, defValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Persistable> T getPersistable(String key, T defValue) {
        return (T) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public byte getByte(String key, byte defValue) {
        return (byte) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public short getShort(String key, short defValue) {
        return (short) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public char getChar(String key, char defValue) {
        return (char) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public double getDouble(String key, double defValue) {
        return (double) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public byte[] getByteArray(String key, byte[] defValue) {
        return (byte[]) fetchStrategy.getValue(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return fetchStrategy.contains(key);
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
                    cacheCandidateProvider,
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
            OnSharedPreferenceChangeListener wrapper = new OnSharedPreferenceChangeListenerWrapper(this, listener);
            eventsBridge.registerOnSharedPreferenceChangeListener(wrapper);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        writeLock.lock();
        try {
            OnSharedPreferenceChangeListener wrapper = new OnSharedPreferenceChangeListenerWrapper(this, listener);
            eventsBridge.unregisterOnSharedPreferenceChangeListener(wrapper);
        } finally {
            writeLock.unlock();
        }
    }
}