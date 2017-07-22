package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performs all submitted tasks in one separated thread sequentially.
 */
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private static final int THREADS_COUNT = 1;
    private static final String THREAD_NAME_PREFIX = "binaryprefs-pool-%s-%s";

    private static final Map<String, ExecutorService> executors = new ConcurrentHashMap<>();
    private static final AtomicInteger threadId = new AtomicInteger();

    private final ExceptionHandler handler;
    private final ExecutorService currentExecutor;

    public ScheduledBackgroundTaskExecutor(final String prefName, final ExceptionHandler handler) {
        this.handler = handler;
        this.currentExecutor = createExecutor(prefName);
    }

    private ExecutorService createExecutor(final String prefName) {
        if (executors.containsKey(prefName)) {
            return executors.get(prefName);
        }
        ThreadFactory factory = createThreadFactory(prefName);
        ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT, factory);
        executors.put(prefName, service);
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

    private Thread createThread(final Runnable r, final String prefName) {
        Thread thread = new Thread(r);
        thread.setName(String.format(THREAD_NAME_PREFIX, prefName, threadId.incrementAndGet()));
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }

    @Override
    public synchronized Completable submit(final Runnable runnable) {
        Future<?> submit = currentExecutor.submit(runnable);
        return new Completable(submit, handler);
    }
}