package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.transaction.ParcelableFileTransactionElement;

interface FileTransactionBridge {
    boolean commit(in ParcelableFileTransactionElement[] elements);
    void apply(in ParcelableFileTransactionElement[] elements);
}