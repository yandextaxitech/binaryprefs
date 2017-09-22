package com.ironz.binaryprefs.init;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.FutureBarrier;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public final class EagerFetchStrategy implements FetchStrategy {

    private final Lock readLock;
    private final TaskExecutor taskExecutor;
    private final CacheProvider cacheProvider;
    private final FileTransaction fileTransaction;
    private final SerializerFactory serializerFactory;

    public EagerFetchStrategy(LockFactory lockFactory,
                              TaskExecutor taskExecutor,
                              CacheProvider cacheProvider,
                              FileTransaction fileTransaction,
                              SerializerFactory serializerFactory) {
        this.readLock = lockFactory.getReadLock();
        this.taskExecutor = taskExecutor;
        this.cacheProvider = cacheProvider;
        this.fileTransaction = fileTransaction;
        this.serializerFactory = serializerFactory;
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
        if (cacheProvider.keys().size() != 0) {
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
    public Object getValue(String key, Object defValue) {
        readLock.lock();
        try {
            Object o = cacheProvider.get(key);
            if (o == null) {
                return defValue;
            }
            return serializerFactory.redefineMutable(o);
        } finally {
            readLock.unlock();
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
                Object redefinedValue = serializerFactory.redefineMutable(value);
                clone.put(key, redefinedValue);
            }
            return Collections.unmodifiableMap(clone);
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
}