package com.ironz.binaryprefs.file.transaction;

/**
 * Describes how serialized content transaction will be performed
 */
public interface FileTransactionExecutor {
    FileElement[] getAllElements();

    void performTransaction(FileElement[] elements);
}