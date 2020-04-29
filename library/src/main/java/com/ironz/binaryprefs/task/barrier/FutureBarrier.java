package com.ironz.binaryprefs.task.barrier;

/**
 * Meta object which holds current task state and allows blocking await.
 */

public interface FutureBarrier<T> {

    /**
     * Complete task without exception handle and re-throws exception on higher level.
     */
    void completeBlockingUnsafe();

    /**
     * Complete task with exception handle and returns result or default value for this task.
     */
    T completeBlockingWihResult(T defValue);

    /**
     * Complete task with result returning without exception handle and re-throws exception on higher level.
     */
    T completeBlockingWithResultUnsafe();

    /**
     * Returns task execution result.
     * Also this method will call exception handle method if task execution fails.
     *
     * @return status - {@code true} if task completed successfully {@code false} otherwise
     */
    boolean completeBlockingWithStatus();
}
