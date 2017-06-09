package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    private static final String SHARED_PREFERENCES_ERROR = "shared_preferences_error";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ExceptionHandler exceptionHandler;

    public ScheduledBackgroundTaskExecutor() {
        this(new ExceptionHandler() {
            @Override
            public void handle(String key, Exception e) {
                //ignored
            }
        });
    }

    public ScheduledBackgroundTaskExecutor(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void submit(Runnable runnable) {
        try {
            executor.submit(runnable);
        } catch (Exception e) {
            exceptionHandler.handle(SHARED_PREFERENCES_ERROR, e);
        }
    }
}