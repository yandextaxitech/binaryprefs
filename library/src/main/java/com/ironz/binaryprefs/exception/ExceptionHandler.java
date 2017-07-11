package com.ironz.binaryprefs.exception;

/**
 * Handles errors for implementing custom behaviour
 */
public interface ExceptionHandler {
    /**
     * Calls while exception are coming (file IO, etc)
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

    ExceptionHandler PRINT = new ExceptionHandler() {
        @Override
        public void handle(Exception e) {
            e.printStackTrace();
        }
    };
}