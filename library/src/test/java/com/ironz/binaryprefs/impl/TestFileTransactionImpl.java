package com.ironz.binaryprefs.impl;

import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.TransactionElement;

public class TestFileTransactionImpl implements FileTransaction {

    private final String baseDir;
    private final FileAdapter fileAdapter;

    public TestFileTransactionImpl(String baseDir, FileAdapter fileAdapter) {
        this.baseDir = baseDir;
        this.fileAdapter = fileAdapter;
    }

    @Override
    public TransactionElement[] fetch() {
        String[] names = fileAdapter.names(baseDir);
        TransactionElement[] elements = new TransactionElement[names.length];
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            int action = TransactionElement.ACTION_FETCH;
            byte[] bytes = fileAdapter.fetch(name);
            elements[i] = new TransactionElement(action, name, bytes);
        }
        return elements;
    }

    @Override
    public boolean commit(TransactionElement[] elements) {
        for (TransactionElement e : elements) {

            int action = e.getAction();
            String name = e.getName();
            byte[] content = e.getContent();

            if (action == TransactionElement.ACTION_UPDATE) {
                fileAdapter.save(name, content);
            }

            if (action == TransactionElement.ACTION_REMOVE) {
                fileAdapter.remove(name);
            }
        }
        return true;
    }
}