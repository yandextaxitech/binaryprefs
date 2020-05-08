package com.ironz.binaryprefs.file.check;

/**
 * Interface for checking availability and another meta of storage
 */
public interface StorageChecker {
    boolean isStorageAvailable();

    boolean isStorageWritable();
}
