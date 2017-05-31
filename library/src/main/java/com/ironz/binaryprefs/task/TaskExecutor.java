package com.ironz.binaryprefs.task;

import java.util.concurrent.Callable;

/**
 * Abstraction for task running
 */
public interface TaskExecutor {
    /**
     * After submitting executor adds this task in queue and runs later
     *
     * @param callable parametrized instance for task execution which returns key of which task been executed
     */
    void submit(Callable<String> callable);

    /**
     * Performs task in current thread and re-throws checked exception
     */
    TaskExecutor DEFAULT = new TaskExecutor() {
        @Override
        public void submit(Callable<String> callable) {
            try {
                callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
}