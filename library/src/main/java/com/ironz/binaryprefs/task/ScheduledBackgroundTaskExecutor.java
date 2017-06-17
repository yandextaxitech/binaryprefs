package com.ironz.binaryprefs.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Performs all submitted tasks in one separated thread sequentially.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ScheduledBackgroundTaskExecutor implements TaskExecutor {

    public static final String SHARED_PREFERENCES_ERROR = "shared_preferences_error";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ErrorExecutionHandler exceptionHandler;

    public ScheduledBackgroundTaskExecutor() {
        this(ErrorExecutionHandler.NO_OP);
    }

    public ScheduledBackgroundTaskExecutor(ErrorExecutionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void submit(Runnable runnable) {
        try {
            executor.submit(runnable);
        } catch (Exception e) {
            exceptionHandler.onFail(SHARED_PREFERENCES_ERROR, e);
        }
    }
}