package com.ironz.binaryprefs.transaction;

/**
 * Describes how serialized content transaction will be performed
 */
public interface ContentTransactionExecutor {
    long beginTransaction();

    void append(long transaction, String name, byte[] content);

    boolean commitTransaction(long transaction);
}
