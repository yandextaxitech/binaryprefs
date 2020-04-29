package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;
import com.ironz.binaryprefs.task.barrierprovider.FutureBarrierProvider;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Performs all submitted tasks in one separated thread sequentially.
 */
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private static final int THREADS_COUNT = 1;
    private static final String THREAD_NAME_PREFIX = "binaryprefs-pool-%s";

    private final ExceptionHandler exceptionHandler;
    private final ExecutorService currentExecutor;
    private final FutureBarrierProvider barrierProvider;

    public ScheduledBackgroundTaskExecutor(String prefName,
                                           ExceptionHandler exceptionHandler,
                                           Map<String, ExecutorService> executors,
                                           FutureBarrierProvider barrierProvider) {
        this.exceptionHandler = exceptionHandler;
        this.currentExecutor = putIfAbsentExecutor(prefName, executors);
        this.barrierProvider = barrierProvider;
    }

    private ExecutorService putIfAbsentExecutor(final String prefName, Map<String, ExecutorService> executors) {
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
        thread.setName(String.format(THREAD_NAME_PREFIX, prefName));
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }

    @Override
    public FutureBarrier<?> submit(final Runnable runnable) {
        Future<?> submit = currentExecutor.submit(runnable);
        return barrierProvider.get(submit, exceptionHandler);
    }

    @Override
    public <T> FutureBarrier<T> submit(Callable<T> callable) {
        Future<T> submit = currentExecutor.submit(callable);
        return barrierProvider.get(submit, exceptionHandler);
    }
}
