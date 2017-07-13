package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;

import java.util.Map;
import java.util.concurrent.*;

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

    private ExecutorService initExecutor(final String prefName) {
        if (executorsMap.containsKey(prefName)) {
            return executorsMap.get(prefName);
        }
        ExecutorService service = Executors.newFixedThreadPool(1, createThreadFactory(prefName));
        executorsMap.put(prefName, service);
        return service;
    }

    private ThreadFactory createThreadFactory(final String prefName) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return createThread(r, prefName);
            }
        };
    }

    private Thread createThread(Runnable r, String prefName) {
        Thread thread = new Thread(r);
        thread.setName("binaryprefs-pool-" + prefName);
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }

    @Override
    public synchronized Completable submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return new Completable(submit, exceptionHandler);
    }
}