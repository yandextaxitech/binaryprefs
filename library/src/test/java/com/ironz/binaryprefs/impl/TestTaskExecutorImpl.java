package com.ironz.binaryprefs.impl;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.task.Completable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.concurrent.*;

public final class TestTaskExecutorImpl implements TaskExecutor {

    private static final ExecutorService executor = CurrentThreadExecutorService.getInstance();

    private final ExceptionHandler exceptionHandler;

    public TestTaskExecutorImpl(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Completable submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return new Completable(submit, exceptionHandler);
    }

    private static final class CurrentThreadExecutorService extends ThreadPoolExecutor {

        private final CountDownLatch signal = new CountDownLatch(1);

        private CurrentThreadExecutorService() {
            super(1, 1, 0, TimeUnit.DAYS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
        }

        @Override
        public void shutdown() {
            super.shutdown();
            signal.countDown();
        }

        static ExecutorService getInstance() {
            return SingletonHolder.instance;
        }

        private static class SingletonHolder {
            static ExecutorService instance = createInstance();
        }

        private static ExecutorService createInstance() {

            final CurrentThreadExecutorService instance = new CurrentThreadExecutorService();

            instance.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        instance.signal.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            return Executors.unconfigurableExecutorService(instance);
        }
    }
}