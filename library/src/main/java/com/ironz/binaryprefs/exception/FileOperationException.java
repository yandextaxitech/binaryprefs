package com.ironz.binaryprefs.exception;


/**
 * Throws if I/O file operations not finished appropriately
 */
public final class FileOperationException extends RuntimeException {

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(Throwable cause) {
        super(cause);
    }
}
