package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;
import com.ironz.binaryprefs.task.barrierprovider.FutureBarrierProvider;

import java.util.concurrent.*;

public final class TestTaskExecutor implements TaskExecutor {

    private final FutureBarrierProvider futureBarrierProvider;
    private final ExceptionHandler exceptionHandler;
    private final ExecutorService executor;

    public TestTaskExecutor(FutureBarrierProvider futureBarrierProvider, ExceptionHandler exceptionHandler) {
        this.futureBarrierProvider = futureBarrierProvider;
        this.exceptionHandler = exceptionHandler;
        executor = currentThreadExecutorService();
    }

    @Override
    public FutureBarrier<?> submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return futureBarrierProvider.get(submit, exceptionHandler);
    }

    @Override
    public <T> FutureBarrier<T> submit(Callable<T> callable) {
        Future<T> submit = executor.submit(callable);
        return futureBarrierProvider.get(submit, exceptionHandler);
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