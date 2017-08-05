package com.ironz.binaryprefs.event;

/**
 * Handles errors for implementing custom behaviour
 */
public interface ExceptionHandler {
    /**
     * Calls while exception are coming (file IO, etc).
     * This method may be called not in the main thread.
     *
     * @param e exception
     */
    void handle(Exception e);

    /**
     * Just ignores any exception
     */
    ExceptionHandler IGNORE = new ExceptionHandler() {
        @Override
        public void handle(Exception ignored) {

        }
    };

    /**
     * Prints stacktrace in logcat
     */
    ExceptionHandler PRINT = new ExceptionHandler() {
        @Override
        public void handle(Exception e) {
            e.printStackTrace();
        }
    };
}