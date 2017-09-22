package com.ironz.binaryprefs.file.transaction;

import java.util.List;
import java.util.Set;

/**
 * Transaction contract which describes one file event mechanism.
 * Contract guarantees what disk changes will be performed
 * successful or rolled back to old values.
 */
public interface FileTransaction {

    /**
     * Acquires global lock for current transaction. {@link #unlock()} - unlocks the current lock.
     */
    void lock();

    /**
     * Release global lock for current transaction which is acquired by {@link #lock()} method.
     */
    void unlock();

    /**
     * Retrieves all file adapter elements and creates {@code byte[]} elements by unique name.
     *
     * @return unique transaction elements.
     */
    List<TransactionElement> fetchAll();

    /**
     * Retrieves all file adapter element names without creating an {@code byte[]}.
     *
     * @return unique transaction elements.
     */
    Set<String> fetchNames();

    /**
     * Retrieves one file adapter element and creates {@code byte[]} element by unique name.
     *
     * @param name file name
     * @return unique transaction element.
     */
    TransactionElement fetchOne(String name);

    /**
     * Performs disk write for all transaction values sequentially.
     *
     * @param elements target elements for transaction.
     */
    void commit(List<TransactionElement> elements);
}