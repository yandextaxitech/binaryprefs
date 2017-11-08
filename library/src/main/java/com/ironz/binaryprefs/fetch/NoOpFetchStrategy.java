package com.ironz.binaryprefs.fetch;

import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.FutureBarrier;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public final class NoOpFetchStrategy implements FetchStrategy {

    private final Lock readLock;
    private final TaskExecutor taskExecutor;
    private final FileTransaction fileTransaction;
    private final SerializerFactory serializerFactory;

    public NoOpFetchStrategy(LockFactory lockFactory,
                             TaskExecutor taskExecutor,
                             FileTransaction fileTransaction,
                             SerializerFactory serializerFactory) {
        this.readLock = lockFactory.getReadLock();
        this.taskExecutor = taskExecutor;
        this.fileTransaction = fileTransaction;
        this.serializerFactory = serializerFactory;
    }

    @Override
    public Object getValue(String key, Object defValue) {
        return getInternal(key, defValue);
    }

    @Override
    public Map<String, Object> getAll() {
        return getAllInternal();
    }

    @Override
    public boolean contains(String key) {
        return containsInternal(key);
    }

    private Object getInternal(final String key, Object defValue) {
        readLock.lock();
        fileTransaction.lock();
        try {
            FutureBarrier barrier = taskExecutor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return fetchObject(key);
                }
            });
            return barrier.completeBlockingWihResult(defValue);
        } finally {
            readLock.unlock();
            fileTransaction.unlock();
        }
    }

    private Map<String, Object> getAllInternal() {
        readLock.lock();
        fileTransaction.lock();
        try {
            Set<String> names = fileTransaction.fetchNames();
            HashMap<String, Object> clone = new HashMap<>(names.size());
            for (String candidate : names) {
                Object o = getInternal(candidate);
                clone.put(candidate, o);
            }
            return Collections.unmodifiableMap(clone);
        } finally {
            fileTransaction.unlock();
            readLock.unlock();
        }
    }

    private Object getInternal(final String key) {
        FutureBarrier barrier = taskExecutor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return fetchObject(key);
            }
        });
        return barrier.completeBlockingWithResultUnsafe();
    }

    private Object fetchObject(String key) {
        TransactionElement element = fileTransaction.fetchOne(key);
        byte[] bytes = element.getContent();
        return serializerFactory.deserialize(key, bytes);
    }

    private boolean containsInternal(String key) {
        readLock.lock();
        try {
            Set<String> names = fileTransaction.fetchNames();
            return names.contains(key);
        } finally {
            readLock.unlock();
        }
    }
}