package com.ironz.binaryprefs.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UninterruptiblesTest {

    // WORKING_SLEEP_DURATION_MILLIS < WAITING_SLEEP_TIMES * WAITING_SLEEP_DURATION_MILLIS
    private static final long WORKING_SLEEP_DURATION_MILLIS = 500L;
    private static final int WAITING_SLEEP_TIMES = 200;
    private static final long WAITING_SLEEP_DURATION_MILLIS = 10L;

    private static final String VALID_RESULT = "VALID_RESULT";
    private static final String INTERRUPTED_RESULT = null;

    private ExecutorService executor;

    @Before
    public void setUp() {
        executor = Executors.newSingleThreadExecutor();
    }

    @After
    public void tearDown() {
        executor.shutdownNow();
        executor = null;
    }

    // region Latch - future is done once latch is count down
    @Test
    public void noInterruptsWithLatch() throws Exception {
        nInterruptsWithLatch(0, 0);
    }

    @Test
    public void singleInterruptWithLatch() throws Exception {
        nInterruptsWithLatch(1, 0);
    }

    @Test
    public void multipleInterruptsWithLatch() throws Exception {
        nInterruptsWithLatch(100, 0);
    }

    @Test
    public void multipleDelayedInterruptsWithLatch() throws Exception {
        nInterruptsWithLatch(100, 10);
    }

    @Test
    public void sanityCheck_plainFutureInterruptsWithLatch() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final Future<String> workingFuture = executor.submit(
                new WorkingLatchCallable(latch));

        final FutureTask<Boolean> waitingTask = new FutureTask<>(
                new WaitingCallable(workingFuture, true));

        final Thread waitingThread = new Thread(waitingTask);
        waitingThread.start();
        waitingThread.interrupt();

        try {
            waitingTask.get();
            fail();
        } catch (ExecutionException expected) {
            // WaitingCallable must throw an InterruptedException
            assertTrue(expected.getCause() instanceof InterruptedException);
        }
    }
    // endregion Latch

    // region Sleep - future is done once sleep finishes
    @Test
    public void noInterruptsWithSleep() throws Exception {
        nInterruptsWithSleep(0, 0);
    }

    @Test
    public void singleInterruptWithSleep() throws Exception {
        nInterruptsWithSleep(1, 0);
    }

    @Test
    public void multipleInterruptsWithSleep() throws Exception {
        nInterruptsWithSleep(100, 0);
    }

    @Test
    public void multipleDelayedInterruptsWithSleep() throws Exception {
        nInterruptsWithSleep(WAITING_SLEEP_TIMES, WAITING_SLEEP_DURATION_MILLIS);
    }

    @Test
    public void sanityCheck_plainFutureInterruptsWithSleep() throws Exception {
        final Future<String> workingFuture = executor.submit(
                new WorkingSleepCallable());

        final FutureTask<Boolean> waitingTask = new FutureTask<>(
                new WaitingCallable(workingFuture, true));

        final Thread waitingThread = new Thread(waitingTask);
        waitingThread.start();
        waitingThread.interrupt();

        try {
            waitingTask.get();
            fail();
        } catch (ExecutionException expected) {
            // WaitingCallable must throw an InterruptedException
            assertTrue(expected.getCause() instanceof InterruptedException);
        }
    }
    // endregion Sleep

    private void nInterruptsWithLatch(int times,
                                      long interruptDelayMillis) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final Future<String> workingFuture = executor.submit(
                new WorkingLatchCallable(latch));

        final FutureTask<Boolean> waitingTask = new FutureTask<>(
                new WaitingCallable(workingFuture, false));

        final Thread waitingThread = new Thread(waitingTask);
        waitingThread.start();

        for (int i = 0; i < times; ++i) {
            waitingThread.interrupt();

            if (interruptDelayMillis > 0) {
                Thread.sleep(interruptDelayMillis);
            }
        }

        latch.countDown();

        final boolean shouldWaitingBeInterrupted = times > 0;
        final boolean wasWaitingInterrupted = waitingTask.get();
        assertEquals(shouldWaitingBeInterrupted, wasWaitingInterrupted);
    }

    private void nInterruptsWithSleep(int times,
                                      long interruptDelayMillis) throws Exception {
        final Future<String> workingFuture = executor.submit(
                new WorkingSleepCallable());

        final FutureTask<Boolean> waitingTask = new FutureTask<>(
                new WaitingCallable(workingFuture, false));

        final Thread waitingThread = new Thread(waitingTask);
        waitingThread.start();

        for (int i = 0; i < times; ++i) {
            waitingThread.interrupt();

            if (interruptDelayMillis > 0) {
                Thread.sleep(interruptDelayMillis);
            }
        }

        final boolean shouldWaitingBeInterrupted = times > 0;
        final boolean wasWaitingInterrupted = waitingTask.get();
        assertEquals(shouldWaitingBeInterrupted, wasWaitingInterrupted);
    }

    /**
     * Finishes once latch is released.
     */
    private static class WorkingLatchCallable implements Callable<String> {

        private final CountDownLatch latch;

        WorkingLatchCallable(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public String call() {
            try {
                latch.await();
                return VALID_RESULT;
            } catch (InterruptedException shouldNotHappen) {
                fail();
            }

            return INTERRUPTED_RESULT;
        }
    }

    /**
     * Finishes after sleeping for {@link #WORKING_SLEEP_DURATION_MILLIS}.
     */
    private static class WorkingSleepCallable implements Callable<String> {

        @Override
        public String call() {
            try {
                Thread.sleep(WORKING_SLEEP_DURATION_MILLIS);
                return VALID_RESULT;
            } catch (InterruptedException shouldNotHappen) {
                fail();
            }

            return null;
        }
    }

    /**
     * Waits for working future to complete,
     * returns thread interrupted status.
     */
    private static class WaitingCallable implements Callable<Boolean> {

        private final Future<String> workingFuture;
        private final boolean allowInterruption;

        WaitingCallable(Future<String> workingFuture,
                        boolean allowInterruption) {

            this.workingFuture = workingFuture;
            this.allowInterruption = allowInterruption;
        }

        @Override
        public Boolean call() throws Exception {
            final String result = allowInterruption
                    ? workingFuture.get()
                    : Uninterruptibles.getUninterruptibly(workingFuture);

            assertEquals(VALID_RESULT, result);

            return Thread.interrupted();
        }
    }
}
