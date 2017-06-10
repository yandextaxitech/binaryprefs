package com.ironz.binaryprefs.exception;

/**
 * Handles errors for implementing custom behaviour
 */
@SuppressWarnings({"unused", "EmptyMethod"})
public interface ExceptionHandler {
    /**
     * Calls while exception are coming (file IO, etc)
     *
     * @param key given key which is operation been fail
     * @param e   exception
     */
    void handle(String key, Exception e);

    /**
     * Just ignores any exception
     */
    ExceptionHandler IGNORE = new ExceptionHandler() {
        @Override
        public void handle(String key, Exception ignored) {

        }
    };
}
