package com.ironz.binaryprefs.transaction;

/**
 * Describes how serialized content transaction will be performed
 */
public interface ContentTransactionExecutor {
    boolean performTransaction(ContentTransaction transaction);
}