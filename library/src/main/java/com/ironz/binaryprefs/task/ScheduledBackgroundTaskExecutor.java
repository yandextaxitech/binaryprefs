package com.ironz.binaryprefs.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void submit(Callable<String> callable) {
        executor.submit(callable);
    }
}