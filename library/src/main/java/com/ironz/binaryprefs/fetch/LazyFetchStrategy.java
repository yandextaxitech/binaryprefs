package com.ironz.binaryprefs.fetch;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
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

public final class LazyFetchStrategy implements FetchStrategy {

    private final Lock readLock;
    private final TaskExecutor taskExecutor;
    private final CacheCandidateProvider candidateProvider;
    private final CacheProvider cacheProvider;
    private final FileTransaction fileTransaction;
    private final SerializerFactory serializerFactory;

    public LazyFetchStrategy(LockFactory lockFactory,
                             TaskExecutor taskExecutor,
                             CacheCandidateProvider candidateProvider,
                             CacheProvider cacheProvider,
                             FileTransaction fileTransaction,
                             SerializerFactory serializerFactory) {
        this.readLock = lockFactory.getReadLock();
        this.taskExecutor = taskExecutor;
        this.candidateProvider = candidateProvider;
        this.cacheProvider = cacheProvider;
        this.fileTransaction = fileTransaction;
        this.serializerFactory = serializerFactory;
        fetchCacheCandidates();
    }

    private void fetchCacheCandidates() {
        readLock.lock();
        try {
            for (String name : fileTransaction.fetchNames()) {
                candidateProvider.put(name);
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object getValue(String key, Object defValue) {
        readLock.lock();
        try {
            Object o = getInternal(key, defValue);
            return serializerFactory.redefineMutable(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Map<String, Object> getAll() {
        readLock.lock();
        try {
            Set<String> candidates = candidateProvider.keys();
            Set<String> keys = cacheProvider.keys();
            if (keys.containsAll(candidates)) {
                Map<String, Object> all = cacheProvider.getAll();
                return Collections.unmodifiableMap(all);
            }
            fileTransaction.lock();
            HashMap<String, Object> clone = new HashMap<>(candidates.size());
            for (String candidate : candidates) {
                if (keys.contains(candidate)) {
                    continue;
                }
                Object o = getInternal(candidate);
                Object redefinedValue = serializerFactory.redefineMutable(o);
                clone.put(candidate, redefinedValue);
            }
            return Collections.unmodifiableMap(clone);
        } finally {
            fileTransaction.unlock();
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(String key) {
        readLock.lock();
        try {
            Set<String> candidates = candidateProvider.keys();
            return candidates.contains(key);
        } finally {
            readLock.unlock();
        }
    }

    private Object getInternal(final String key) {
        Object cached = cacheProvider.get(key);
        if (cached != null) {
            return cached;
        }
        FutureBarrier barrier = taskExecutor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return fetchObject(key);
            }
        });
        return barrier.completeBlockingWithResultUnsafe();
    }

    private Object getInternal(final String key, Object defValue) {
        Object cached = cacheProvider.get(key);
        if (cached != null) {
            return cached;
        }
        Set<String> names = candidateProvider.keys();
        if (!names.contains(key)) {
            return defValue;
        }
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
            fileTransaction.unlock();
        }
    }

    private Object fetchObject(String key) {
        TransactionElement element = fileTransaction.fetchOne(key);
        byte[] bytes = element.getContent();
        Object deserialize = serializerFactory.deserialize(key, bytes);
        cacheProvider.put(key, deserialize);
        return deserialize;
    }
}