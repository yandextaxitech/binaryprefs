package com.ironz.binaryprefs.init;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.FutureBarrier;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public final class LazyFetchStrategy implements FetchStrategy {

    private final Lock readLock;
    private final TaskExecutor taskExecutor;
    private final CacheProvider cacheProvider;
    private final FileTransaction fileTransaction;
    private final SerializerFactory serializerFactory;

    public LazyFetchStrategy(LockFactory lockFactory,
                             TaskExecutor taskExecutor,
                             CacheProvider cacheProvider,
                             FileTransaction fileTransaction,
                             SerializerFactory serializerFactory) {
        this.readLock = lockFactory.getReadLock();
        this.taskExecutor = taskExecutor;
        this.cacheProvider = cacheProvider;
        this.fileTransaction = fileTransaction;
        this.serializerFactory = serializerFactory;
    }

    @Override
    public Object getValue(String key, Object defValue) {
        readLock.lock();
        try {
            return getInternal(key, defValue);
        } finally {
            readLock.unlock();
        }
    }

    private Object getInternal(final String key, Object defValue) {
        Object cached = cacheProvider.get(key);
        if (cached != null) {
            return cached;
        }
        // TODO: 9/19/17 implement transaction acquire lock here
        Set<String> names = fileTransaction.fetchNames();
        if (!names.contains(key)) {
            return defValue;
        }
        FutureBarrier barrier = taskExecutor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                TransactionElement element = fileTransaction.fetchOne(key);
                byte[] bytes = element.getContent();
                Object deserialize = serializerFactory.deserialize(key, bytes);
                cacheProvider.put(key, deserialize);
                return deserialize;
            }
        });
        return barrier.completeBlockingWihResult(defValue);
        // TODO: 9/19/17 implement transaction lock release here
    }
}