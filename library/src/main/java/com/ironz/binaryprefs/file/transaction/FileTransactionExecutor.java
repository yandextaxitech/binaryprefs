package com.ironz.binaryprefs.file.transaction;

/**
 * Describes how serialized content transaction will be performed
 */
public interface FileTransactionExecutor {
    FileElement[] getAll();

    void update(FileElement[] elements);

    void remove(String[] names);
}