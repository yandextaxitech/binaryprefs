package com.ironz.binaryprefs.task;

/**
 * Interface which describes fail event listener
 */
public interface ErrorExecutionHandler {
    /**
     * Calls after task has been failed
     *
     * @param key target key for which task has not been completed
     * @param e   exception which was thrown while task been in progress
     */
    void onFail(String key, Exception e);

    /**
     * Implementation which do nothing
     */
    ErrorExecutionHandler NO_OP = new ErrorExecutionHandler() {
        @Override
        public void onFail(String key, Exception e) {

        }
    };
}