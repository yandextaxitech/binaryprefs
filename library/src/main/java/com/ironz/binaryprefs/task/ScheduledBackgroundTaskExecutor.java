package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Performs all submitted tasks in one separated thread sequentially.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private final ExceptionHandler exceptionHandler;

    private static final Map<String, ExecutorService> executorsMap = new ConcurrentHashMap<>();

    private final ExecutorService executor;

    public ScheduledBackgroundTaskExecutor(String prefName, ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        executor = initExecutor(prefName);
    }

    private ExecutorService initExecutor(String prefName) {
        if (executorsMap.containsKey(prefName)) {
            return executorsMap.get(prefName);
        }
        ExecutorService service = Executors.newSingleThreadExecutor();
        executorsMap.put(prefName, service);
        return service;
    }

    @Override
    public Completable submit(Runnable runnable) {
        synchronized (ScheduledBackgroundTaskExecutor.class) {
            Future<?> submit = executor.submit(runnable);
            return new Completable(submit, exceptionHandler);
        }
    }
}