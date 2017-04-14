package com.ironz.binaryprefs.exception;


/**
 * Throws if I/O file operations not finished appropriately
 */
public final class FileOperationException extends RuntimeException {

    /**
     */
    public FileOperationException(Throwable cause) {
        super(cause);
    }
}
