package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;

import java.util.concurrent.*;

public final class TestTaskExecutorImpl implements TaskExecutor {

    private final ExceptionHandler exceptionHandler;
    private final ExecutorService executor;

    public TestTaskExecutorImpl(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        executor = currentThreadExecutorService();
    }

    @Override
    public FutureBarrier submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return new FutureBarrier(submit, exceptionHandler);
    }

    @Override
    public FutureBarrier submit(Callable<?> callable) {
        Future<?> submit = executor.submit(callable);
        return new FutureBarrier(submit, exceptionHandler);
    }

    private ExecutorService currentThreadExecutorService() {
        final ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        return new ThreadPoolExecutor(0, 1, 0L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), callerRunsPolicy) {
            @Override
            public void execute(Runnable command) {
                callerRunsPolicy.rejectedExecution(command, this);
            }
        };
    }
}