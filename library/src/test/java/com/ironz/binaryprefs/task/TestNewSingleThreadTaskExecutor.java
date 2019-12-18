package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Submits tasks on a single thread, which is not a current thread.
 */
public class TestNewSingleThreadTaskExecutor implements TaskExecutor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExceptionHandler exceptionHandler;

    public TestNewSingleThreadTaskExecutor(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public FutureBarrier<?> submit(Runnable runnable) {
        return new FutureBarrier<>(
                executor.submit(runnable),
                exceptionHandler
        );
    }

    @Override
    public <T> FutureBarrier<T> submit(Callable<T> callable) {
        return new FutureBarrier<>(
                executor.submit(callable),
                exceptionHandler
        );
    }
}
