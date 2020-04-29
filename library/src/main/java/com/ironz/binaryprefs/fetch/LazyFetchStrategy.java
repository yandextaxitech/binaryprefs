package com.ironz.binaryprefs.fetch;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;

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
        return getValueInternal(key, defValue);
    }

    @Override
    public Map<String, Object> getAll() {
        return getAllInternal();
    }

    @Override
    public boolean contains(String key) {
        return containsInternal(key);
    }

    private Object getValueInternal(String key, Object defValue) {
        readLock.lock();
        try {
            Object o = getInternal(key, defValue);
            return serializerFactory.redefineMutable(o);
        } finally {
            readLock.unlock();
        }
    }

    private Object getInternal(final String key, Object defValue) {
        Object cached = cacheProvider.get(key);
        if (cached != null) {
            return cached;
        }
        Set<String> candidates = candidateProvider.keys();
        if (!candidates.contains(key)) {
            return defValue;
        }
        FutureBarrier<Object> barrier = taskExecutor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return fetchOneFromDiskLocked(key);
            }
        });
        return barrier.completeBlockingWihResult(defValue);
    }

    private Map<String, Object> getAllInternal() {
        readLock.lock();
        try {
            Set<String> candidates = candidateProvider.keys();
            Set<String> cachedKeys = cacheProvider.keys();
            Map<String, Object> allCache = cacheProvider.getAll();
            if (cachedKeys.containsAll(candidates)) {
                return Collections.unmodifiableMap(allCache);
            }
            Map<String, Object> fetched = fetchDeltaTask(candidates, cachedKeys);
            Map<String, Object> merged = mergeCache(fetched, allCache);
            return Collections.unmodifiableMap(merged);
        } finally {
            readLock.unlock();
        }
    }

    private Map<String, Object> mergeCache(Map<String, Object> fetched, Map<String, Object> allCache) {
        int totalCacheSize = fetched.size() + allCache.size();
        Map<String, Object> map = new HashMap<>(totalCacheSize);
        map.putAll(fetched);
        map.putAll(allCache);
        return map;
    }

    private Map<String, Object> fetchDeltaTask(final Set<String> candidates, final Set<String> cachedKeys) {
        FutureBarrier<Map<String, Object>> barrier = taskExecutor.submit(new Callable<Map<String, Object>>() {
            @Override
            public Map<String, Object> call() throws Exception {
                return fetchDeltaLocked(candidates, cachedKeys);
            }
        });
        return barrier.completeBlockingWithResultUnsafe();
    }

    private Map<String, Object> fetchDeltaLocked(Set<String> candidates, Set<String> cachedKeys) {
        fileTransaction.lock();
        try {
            Map<String, Object> map = new HashMap<>();
            for (String candidate : candidates) {
                if (cachedKeys.contains(candidate)) {
                    continue;
                }
                Object o = fetchOneFromDisk(candidate);
                map.put(candidate, o);
            }
            return map;
        } finally {
            fileTransaction.unlock();
        }
    }

    private Object fetchOneFromDiskLocked(String key) {
        fileTransaction.lock();
        try {
            return fetchOneFromDisk(key);
        } finally {
            fileTransaction.unlock();
        }
    }

    private Object fetchOneFromDisk(String key) {
        TransactionElement element = fileTransaction.fetchOne(key);
        byte[] bytes = element.getContent();
        Object deserialize = serializerFactory.deserialize(key, bytes);
        cacheProvider.put(key, deserialize);
        return deserialize;
    }

    private boolean containsInternal(String key) {
        readLock.lock();
        try {
            Set<String> candidates = candidateProvider.keys();
            return candidates.contains(key);
        } finally {
            readLock.unlock();
        }
    }
}