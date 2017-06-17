package com.ironz.binaryprefs.task;

/**
 * Abstraction for task running. You should guarantee sequential task execution.
 */
public interface TaskExecutor {
    /**
     * After submitting executor adds this task in queue and runs later.
     * Tasks guaranteed to execute sequentially.
     *
     * @param runnable instance for task execution
     */
    void submit(Runnable runnable);

    /**
     * Performs task in current thread
     */
    TaskExecutor DEFAULT = new TaskExecutor() {
        @Override
        public void submit(Runnable runnable) {
            runnable.run();
        }
    };
}