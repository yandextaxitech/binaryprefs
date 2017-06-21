package com.ironz.binaryprefs.transaction;

/**
 * Describes how file transaction will be performed
 */
public interface TransactionExecutor {
    long beginTransaction(String key);

    void append(long transaction, String name, byte[] content);

    boolean commitTransaction(long transaction);
}
