package com.ironz.binaryprefs.exception;

/**
 * Handles errors for implementing custom behaviour
 */
@SuppressWarnings({"unused", "EmptyMethod"})
public interface ExceptionHandler {
    /**
     * Calls while exception are coming (file IO, etc)
     *
     * @param e   exception
     * @param key given key which is operation been fail
     */
    void handle(Exception e, String key);

    ExceptionHandler EMPTY = new ExceptionHandler() {
        @Override
        public void handle(Exception ignored, String key) {

        }
    };
}
