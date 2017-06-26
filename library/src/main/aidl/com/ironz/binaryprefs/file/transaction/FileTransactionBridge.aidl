package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.transaction.FileTransactionElement;

interface FileTransactionBridge {
    FileTransactionElement[] fetch();
    boolean commit(in FileTransactionElement[] elements);
}