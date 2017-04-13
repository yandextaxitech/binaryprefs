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
}
