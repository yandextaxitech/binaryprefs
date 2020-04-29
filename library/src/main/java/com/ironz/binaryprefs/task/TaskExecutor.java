package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.task.barrier.FutureBarrier;

import java.util.concurrent.Callable;

/**
 * Abstraction for task running. You should guarantee sequential task execution.
 */
public interface TaskExecutor {
    /**
     * Submits runnable into task executor.
     * After submitting executor adds this task in
     * queue and runs later, tasks guaranteed
     * to be executed sequentially.
     *
     * @param runnable instance for task execution
     * @return future barrier for task blocking
     */
    FutureBarrier<?> submit(Runnable runnable);

    /**
     * Submits callable into task executor.
     * After submitting executor adds this task in
     * queue and runs later, tasks guaranteed
     * to be executed sequentially.
     *
     * @param callable instance for task execution
     * @return future barrier for task blocking
     */
    <T> FutureBarrier<T> submit(Callable<T> callable);
}