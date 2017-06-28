package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;
import com.ironz.binaryprefs.serialization.strategy.impl.*;
import com.ironz.binaryprefs.task.Completable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.*;
import java.util.concurrent.locks.Lock;

@SuppressWarnings("WeakerAccess")
final class BinaryPreferencesEditor implements PreferencesEditor {

    private static final TransactionElement[] EMPTY_TRANSACTION_ARRAY = new TransactionElement[0];

    private final Map<String, SerializationStrategy> strategyMap = new HashMap<>(0);
    private final Set<String> removeSet = new HashSet<>(0);

    private final Preferences preferences;
    private final FileTransaction fileTransaction;
    private final EventBridge bridge;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final CacheProvider cacheProvider;
    private final Lock writeLock;
    private final ByteEncryption byteEncryption;

    private boolean clear;

    BinaryPreferencesEditor(Preferences preferences,
                            FileTransaction fileTransaction,
                            EventBridge bridge,
                            TaskExecutor taskExecutor,
                            SerializerFactory serializerFactory,
                            CacheProvider cacheProvider,
                            Lock writeLock,
                            ByteEncryption byteEncryption) {
        this.preferences = preferences;
        this.fileTransaction = fileTransaction;
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
            clear = true;
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
                    transact();
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
            Completable submit = taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    transact();
                }
            });
            return submit.completeBlocking();
        } finally {
            writeLock.unlock();
        }
    }

    private void clearCache() {
        if (!clear) {
            return;
        }
        for (String name : cacheProvider.keys()) {
            cacheProvider.remove(name);
        }
    }

    private void removeCache() {
        if (clear) {
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

    private void transact() {
        List<TransactionElement> transaction = createTransaction();
        fileTransaction.commit(transaction);
        notifyListeners(transaction);
    }

    private List<TransactionElement> createTransaction() {
        ArrayList<TransactionElement> elements = new ArrayList<>();
        elements.addAll(clearPersistence());
        elements.addAll(removePersistence());
        elements.addAll(storePersistence());
        return elements;
    }

    private List<TransactionElement> clearPersistence() {
        if (!clear) {
            return Collections.emptyList();
        }
        List<TransactionElement> elements = new ArrayList<>();
        for (String name : cacheProvider.keys()) {
            TransactionElement e = TransactionElement.createRemoveElement(name);
            elements.add(e);
        }
        return elements;
    }

    private List<TransactionElement> removePersistence() {
        if (clear) {
            return Collections.emptyList();
        }
        List<TransactionElement> elements = new ArrayList<>();
        for (String name : removeSet) {
            TransactionElement e = TransactionElement.createRemoveElement(name);
            elements.add(e);
        }
        return elements;
    }

    private List<TransactionElement> storePersistence() {
        Set<String> strings = strategyMap.keySet();
        List<TransactionElement> elements = new ArrayList<>(strings.size());
        for (String key : strings) {
            SerializationStrategy strategy = strategyMap.get(key);
            byte[] bytes = strategy.serialize();
            byte[] encrypt = byteEncryption.encrypt(bytes);
            TransactionElement e = TransactionElement.createUpdateElement(key, encrypt);
            elements.add(e);
        }
        return elements;
    }

    private void notifyListeners(List<TransactionElement> transaction) {
        for (TransactionElement element : transaction) {
            String name = element.getName();
            byte[] bytes = element.getContent();
            if (element.getAction() == TransactionElement.ACTION_REMOVE) {
                bridge.notifyListenersRemove(preferences, name);
            }
            if (element.getAction() == TransactionElement.ACTION_UPDATE) {
                bridge.notifyListenersUpdate(preferences, name, bytes);
            }
        }
    }
}