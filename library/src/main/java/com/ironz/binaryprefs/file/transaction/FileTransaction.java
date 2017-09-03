package com.ironz.binaryprefs.file.transaction;

import java.util.List;

/**
 * Transaction contract which describes one file event mechanism.
 * Contract guarantees what disk changes will be performed
 * successful or rolled back to old values.
 */
public interface FileTransaction {
    /**
     * Retrieves all file adapter elements and creates {@code byte[]} elements by unique name.
     *
     * @return unique transaction elements.
     */
    List<TransactionElement> fetchAll();

    /**
     * Retrieves one file adapter element and creates {@code byte[]} element by unique name.
     *
     * @param name file name
     * @return unique transaction element.
     */
    TransactionElement fetchOne(String name);

    /**
     * Retrieves all file adapter element names without creating an {@code byte[]}.
     *
     * @return unique transaction elements.
     */
    List<TransactionElement> fetchNames();

    /**
     * Performs disk write for all transaction values sequentially.
     *
     * @param elements target elements for transaction.
     */
    void commit(List<TransactionElement> elements);
}