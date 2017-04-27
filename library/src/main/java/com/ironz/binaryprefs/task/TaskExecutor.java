package com.ironz.binaryprefs.task;

/**
 * Abstraction for task running
 */
public interface TaskExecutor {
    /**
     * After submitting executor add this task in queue
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