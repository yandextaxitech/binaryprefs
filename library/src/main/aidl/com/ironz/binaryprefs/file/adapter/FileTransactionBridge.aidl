package com.ironz.binaryprefs.file.adapter;

import com.ironz.binaryprefs.file.adapter.FileTransactionElement;

interface FileTransactionBridge {
    String[] names();
    byte[] fetch(String name);
    boolean commit(in FileTransactionElement[] elements);
    void apply(in FileTransactionElement[] elements);
}