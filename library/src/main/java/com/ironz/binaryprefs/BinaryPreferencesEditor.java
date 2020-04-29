package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
import com.ironz.binaryprefs.event.EventBridge;
import com.ironz.binaryprefs.exception.TransactionInvalidatedException;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;
import com.ironz.binaryprefs.serialization.strategy.impl.*;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.*;
import java.util.concurrent.locks.Lock;

final class BinaryPreferencesEditor implements PreferencesEditor {

    private static final String TRANSACTED_TWICE_MESSAGE = "Transaction should be applied or committed only once!";

    private final Map<String, SerializationStrategy> strategyMap = new HashMap<>();
    private final Set<String> removeSet = new HashSet<>();

    private final FileTransaction fileTransaction;
    private final EventBridge bridge;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final CacheProvider cacheProvider;
    private final CacheCandidateProvider candidateProvider;
    private final Lock writeLock;

    private boolean invalidated;

    BinaryPreferencesEditor(FileTransaction fileTransaction,
                            EventBridge bridge,
                            TaskExecutor taskExecutor,
                            SerializerFactory serializerFactory,
                            CacheProvider cacheProvider,
                            CacheCandidateProvider candidateProvider,
                            Lock writeLock) {
        this.fileTransaction = fileTransaction;
        this.bridge = bridge;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        this.cacheProvider = cacheProvider;
        this.candidateProvider = candidateProvider;
        this.writeLock = writeLock;
    }

    @Override
    public PreferencesEditor putString(String key, String value) {
        if (value == null) {
            return remove(key);
        }
        writeLock.lock();
        try {
            SerializationStrategy strategy = new StringSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new StringSetSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new IntegerSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new LongSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new FloatSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new BooleanSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new PersistableSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new ByteSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new ShortSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new CharSerializationStrategy(value, serializerFactory);
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
            SerializationStrategy strategy = new DoubleSerializationStrategy(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public PreferencesEditor putByteArray(String key, byte[] value) {
        writeLock.lock();
        try {
            SerializationStrategy strategy = new ByteArraySerializationStrategy(value, serializerFactory);
            strategyMap.put(key, strategy);
            return this;
        } finally {
            writeLock.unlock();
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
            Set<String> all = candidateProvider.keys();
            removeSet.addAll(all);
            return this;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void apply() {
        writeLock.lock();
        try {
            performTransaction();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean commit() {
        writeLock.lock();
        try {
            FutureBarrier barrier = performTransaction();
            return barrier.completeBlockingWithStatus();
        } finally {
            writeLock.unlock();
        }
    }

    private FutureBarrier performTransaction() {
        removeCache();
        storeCache();
        invalidate();
        return taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                commitTransaction();
            }
        });
    }

    private void removeCache() {
        for (String name : removeSet) {
            candidateProvider.remove(name);
            cacheProvider.remove(name);
        }
    }

    private void storeCache() {
        for (String name : strategyMap.keySet()) {
            SerializationStrategy strategy = strategyMap.get(name);
            Object value = strategy.getValue();
            candidateProvider.put(name);
            cacheProvider.put(name, value);
        }
    }

    private void invalidate() {
        if (invalidated) {
            throw new TransactionInvalidatedException(TRANSACTED_TWICE_MESSAGE);
        }
        invalidated = true;
    }

    private void commitTransaction() {
        List<TransactionElement> transaction = createTransaction();
        fileTransaction.commit(transaction);
        notifyListeners(transaction);
    }

    private List<TransactionElement> createTransaction() {
        List<TransactionElement> elements = new LinkedList<>();
        elements.addAll(removePersistence());
        elements.addAll(storePersistence());
        return elements;
    }

    private List<TransactionElement> removePersistence() {
        List<TransactionElement> elements = new LinkedList<>();
        for (String name : removeSet) {
            TransactionElement e = TransactionElement.createRemovalElement(name);
            elements.add(e);
        }
        return elements;
    }

    private List<TransactionElement> storePersistence() {
        Set<String> strings = strategyMap.keySet();
        List<TransactionElement> elements = new LinkedList<>();
        for (String name : strings) {
            SerializationStrategy strategy = strategyMap.get(name);
            byte[] bytes = strategy.serialize();
            TransactionElement e = TransactionElement.createUpdateElement(name, bytes);
            elements.add(e);
        }
        return elements;
    }

    private void notifyListeners(List<TransactionElement> transaction) {
        for (TransactionElement element : transaction) {
            String name = element.getName();
            byte[] bytes = element.getContent();
            if (element.getAction() == TransactionElement.ACTION_REMOVE) {
                bridge.notifyListenersRemove(name);
            }
            if (element.getAction() == TransactionElement.ACTION_UPDATE) {
                bridge.notifyListenersUpdate(name, bytes);
            }
        }
    }
}