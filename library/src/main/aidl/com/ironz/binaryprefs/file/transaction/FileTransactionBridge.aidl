package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.transaction.ParcelableFileTransactionElement;

interface FileTransactionBridge {
    String[] names();
    byte[] fetch(String name);
    boolean commit(in ParcelableFileTransactionElement[] elements);
    void apply(in ParcelableFileTransactionElement[] elements);
}