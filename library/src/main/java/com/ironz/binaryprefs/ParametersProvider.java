package com.ironz.binaryprefs;

import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

final class ParametersProvider {

    private static final Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
    private static final Map<String, Lock> processLocks = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Object>> caches = new ConcurrentHashMap<>();
    private static final Map<String, List<SharedPreferences.OnSharedPreferenceChangeListener>> allListeners = new ConcurrentHashMap<>();
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

    Map<String, List<SharedPreferences.OnSharedPreferenceChangeListener>> getAllListeners() {
        return allListeners;
    }

    Map<String, ExecutorService> getExecutors() {
        return executors;
    }
}