package com.ironz.binaryprefs.exception;

/**
 * Exception will be thrown if lock acquire/release operations fails
 */
public final class LockOperationException extends RuntimeException {

    public LockOperationException(Throwable cause) {
        super(cause);
    }
}