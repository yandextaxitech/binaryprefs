package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.adapter.FileAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class MultiProcessTransactionImpl implements FileTransaction {

    private static final String COMMIT = "commit";

    private final FileAdapter fileAdapter;

    public MultiProcessTransactionImpl(FileAdapter fileAdapter) {
        this.fileAdapter = fileAdapter;
    }

    @Override
    public List<TransactionElement> fetch() {
        String[] names = fileAdapter.names();
        List<TransactionElement> elements = new ArrayList<>();
        for (String name : names) {
            byte[] bytes = fileAdapter.fetch(name);
            TransactionElement element = TransactionElement.createFetchElement(name, bytes);
            elements.add(element);
        }
        return elements;
    }

    @Override
    public void commit(List<TransactionElement> elements) {
        for (TransactionElement element : elements) {
            int action = element.getAction();
            String name = element.getName();
            byte[] content = element.getContent();
            if (action == TransactionElement.ACTION_UPDATE) {
                fileAdapter.save(name, content);
            }
            if (action == TransactionElement.ACTION_REMOVE) {
                fileAdapter.remove(name);
            }
        }
    }
}