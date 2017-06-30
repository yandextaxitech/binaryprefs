package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Performs all submitted tasks in one separated thread sequentially.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private final ExceptionHandler exceptionHandler;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            thread.setName(ScheduledBackgroundTaskExecutor.class.getSimpleName() + "-thread");
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        }
    });

    public ScheduledBackgroundTaskExecutor(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public synchronized Completable submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return new Completable(submit, exceptionHandler);
    }
}