package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Performs all submitted tasks in one separated thread sequentially.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private final ExceptionHandler exceptionHandler;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScheduledBackgroundTaskExecutor(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public synchronized Completable submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return new Completable(submit, exceptionHandler);
    }
}