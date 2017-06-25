package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;
import com.ironz.binaryprefs.serialization.strategy.impl.*;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

@SuppressWarnings("WeakerAccess")
final class BinaryPreferencesEditor implements PreferencesEditor {

    public static final String SAVE = "save";

    private final Map<String, SerializationStrategy> strategyMap = new HashMap<>(0);
    private final Set<String> removeSet = new HashSet<>(0);

    private final Preferences preferences;
    private final ExceptionHandler exceptionHandler;
    private final FileAdapter fileAdapter;
    private final EventBridge bridge;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final CacheProvider cacheProvider;
    private final Lock writeLock;
    private final ByteEncryption byteEncryption;

    private boolean clearFlag;

    BinaryPreferencesEditor(Preferences preferences,
                            FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            EventBridge bridge,
                            TaskExecutor taskExecutor,
                            SerializerFactory serializerFactory,
                            CacheProvider cacheProvider,
                            Lock writeLock,
                            ByteEncryption byteEncryption) {
        this.preferences = preferences;
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.bridge = bridge;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        this.cacheProvider = cacheProvider;
        this.writeLock = writeLock;
        this.byteEncryption = byteEncryption;
    }

    @Override
    public PreferencesEditor putString(String key, String value) {
        if (value == null) {
            return remove(key);
        }
        writeLock.lock();
        try {
            SerializationStrategy strategy = new StringSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putStringSet(String key, Set<String> value) {
        if (value == null) {
            return remove(key);
        }
        writeLock.lock();
        try {
            SerializationStrategy strategy = new StringSetSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putInt(String key, int value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new IntegerSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putLong(String key, long value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new LongSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putFloat(String key, float value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new FloatSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putBoolean(String key, boolean value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new BooleanSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public <T extends Persistable> PreferencesEditor putPersistable(String key, T value) {
        if (value == null) {
            return remove(key);
        }
        writeLock.lock();
        try {
            SerializationStrategy strategy = new PersistableSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putByte(String key, byte value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new ByteSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putShort(String key, short value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new ShortSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putChar(String key, char value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new CharSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putDouble(String key, double value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new DoubleSerializationStrategyImpl(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.lock();
        }
    }

    @Override
    public PreferencesEditor remove(String key) {
        writeLock.lock();
        try {
            removeSet.add(key);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor clear() {
        writeLock.lock();
        try {
            clearFlag = true;
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void apply() {
        writeLock.lock();
        try {
            clearCache();
            removeCache();
            storeCache();
            taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    saveAll();
                }
            });
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean commit() {
        writeLock.lock();
        try {
            clearCache();
            removeCache();
            storeCache();
            return saveAll();
        } finally {
            writeLock.unlock();
        }
    }

    private boolean saveAll() {
        try {
            clearPersistence();
            removePersistence();
            storePersistence();
            return true;
        } catch (Exception e) {
            exceptionHandler.handle(SAVE, e);
        }
        return false;
    }

    private void clearCache() {
        if (!clearFlag) {
            return;
        }
        for (String name : cacheProvider.keys()) {
            cacheProvider.remove(name);
        }
    }

    private void removeCache() {
        if (clearFlag) {
            return;
        }
        for (String name : removeSet) {
            cacheProvider.remove(name);
        }
    }

    private void storeCache() {
        for (String name : strategyMap.keySet()) {
            SerializationStrategy strategy = strategyMap.get(name);
            Object value = strategy.getValue();
            cacheProvider.put(name, value);
        }
    }

    private void clearPersistence() {
        if (!clearFlag) {
            return;
        }
        for (String name : cacheProvider.keys()) {
            removeInternal(name);
        }
    }

    private void removePersistence() {
        for (String name : removeSet) {
            removeInternal(name);
        }
    }

    private void storePersistence() {
        for (String key : strategyMap.keySet()) {
            SerializationStrategy strategy = strategyMap.get(key);
            storeInternal(key, strategy);
        }
    }

    private void removeInternal(String name) {
        fileAdapter.remove(name);
        bridge.notifyListenersRemove(preferences, name);
    }

    private void storeInternal(String name, SerializationStrategy strategy) {
        Object value = strategy.getValue();
        byte[] bytes = strategy.serialize();
        byte[] encrypt = byteEncryption.encrypt(bytes);
        fileAdapter.save(name, encrypt);
        bridge.notifyListenersUpdate(preferences, name, value);
    }
}