package com.ironz.binaryprefs.exception;

/**
 * Exception will be thrown if encrypted data will not be performed correctly
 */
public final class EncryptionException extends RuntimeException {

    public EncryptionException(Throwable cause) {
        super(cause);
    }

    public EncryptionException(String msg) {
        super(msg);
    }
}