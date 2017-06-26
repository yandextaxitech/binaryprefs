package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.transaction.FileTransactionElement;

interface FileTransactionBridge {
    String[] names();
    byte[] fetch(String name);
    boolean commit(in FileTransactionElement[] elements);
    void apply(in FileTransactionElement[] elements);
}