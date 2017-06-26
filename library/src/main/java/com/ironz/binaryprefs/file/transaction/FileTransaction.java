package com.ironz.binaryprefs.file.transaction;

public interface FileTransaction {
    String[] names();

    byte[] fetch(String name);

    void apply(TransactionElement[] elements);

    boolean commit(TransactionElement[] elements);
}