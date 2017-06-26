package com.ironz.binaryprefs.file.transaction;

public interface FileTransaction {
    TransactionElement[] fetch();
    boolean commit(TransactionElement[] elements);
}