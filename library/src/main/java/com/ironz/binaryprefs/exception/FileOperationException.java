package com.ironz.binaryprefs.exception;


/**
 * Throws if I/O file operations not finished appropriately
 */
public class FileOperationException extends RuntimeException {

    /**
     * @param cause base cause
     */
    public FileOperationException(Throwable cause) {
        super(cause);
    }
}
