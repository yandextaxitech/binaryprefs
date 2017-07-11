package com.ironz.binaryprefs.exception;


/**
 * Exception will be thrown if I/O file operations not finished appropriately
 */
public final class FileOperationException extends RuntimeException {

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(Throwable cause) {
        super(cause);
    }
}