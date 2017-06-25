package com.ironz.binaryprefs.file.transaction;

/**
 * Describes how serialized content transaction will be performed
 */
public interface FileTransactionExecutor {
    FileTransactionElement[] getAll();

    void update(FileTransactionElement[] elements);

    void remove(String[] names);
}