package com.ironz.binaryprefs.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public <T> void submit(Callable<T> callable) {
        executor.submit(callable);
    }
}