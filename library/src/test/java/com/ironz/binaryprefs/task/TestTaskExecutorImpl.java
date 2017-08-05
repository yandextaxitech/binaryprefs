package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;

import java.util.Map;
import java.util.concurrent.*;

public final class TestTaskExecutorImpl implements TaskExecutor {

    private final ExceptionHandler exceptionHandler;

    private static final Map<String, ExecutorService> executorsMap = new ConcurrentHashMap<>();

    private final ExecutorService executor;

    public TestTaskExecutorImpl(String prefName, ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        executor = initExecutor(prefName);
    }

    private ExecutorService initExecutor(String prefName) {
        if (executorsMap.containsKey(prefName)) {
            return executorsMap.get(prefName);
        }
        ExecutorService service = CurrentThreadExecutorService.newExecutor();
        executorsMap.put(prefName, service);
        return service;
    }

    @Override
    public FutureBarrier submit(Runnable runnable) {
        Future<?> submit = executor.submit(runnable);
        return new FutureBarrier(submit, exceptionHandler);
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

        private static ExecutorService newExecutor() {

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