package com.ironz.binaryprefs;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

final class ParametersProvider {

    private static final Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
    private static final Map<String, Lock> processLocks = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Object>> caches = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> cacheCandidates = new ConcurrentHashMap<>();
    private static final Map<String, List<OnSharedPreferenceChangeListener>> allListeners = new ConcurrentHashMap<>();
    private static final Map<String, ExecutorService> executors = new ConcurrentHashMap<>();

    Map<String, ReadWriteLock> getLocks() {
        return locks;
    }

    Map<String, Lock> getProcessLocks() {
        return processLocks;
    }

    Map<String, Map<String, Object>> getCaches() {
        return caches;
    }

    Map<String, List<OnSharedPreferenceChangeListener>> getAllListeners() {
        return allListeners;
    }

    Map<String, ExecutorService> getExecutors() {
        return executors;
    }

    Map<String, Set<String>> getCacheCandidates() {
        return cacheCandidates;
    }
}