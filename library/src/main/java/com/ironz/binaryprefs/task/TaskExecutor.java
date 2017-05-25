package com.ironz.binaryprefs.task;

import java.util.concurrent.Callable;

/**
 * Abstraction for task running
 */
public interface TaskExecutor {
    /**
     * After submitting executor adds this task in queue and runs later
     *
     * @param callable instance for task execution
     */
    <T> void submit(Callable<T> callable);

    /**
     * Performs task in current thread and re-throws checked exception
     */
    TaskExecutor DEFAULT = new TaskExecutor() {
        @Override
        public <T> void submit(Callable<T> callable) {
            try {
                callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}