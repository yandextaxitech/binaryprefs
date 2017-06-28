package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.adapter.FileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class MultiProcessTransactionImpl implements FileTransaction {

    private static final String COMMIT = "commit";

    private final String baseDir;
    private final FileAdapter fileAdapter;

    public MultiProcessTransactionImpl(String baseDir, FileAdapter fileAdapter) {
        this.baseDir = baseDir;
        this.fileAdapter = fileAdapter;
    }

    @Override
    public List<TransactionElement> fetch() {
        String[] names = fileAdapter.names(baseDir);
        List<TransactionElement> elements = new ArrayList<>();
        for (String name : names) {
            File file = new File(baseDir, name);
            String path = file.getAbsolutePath();
            byte[] bytes = fileAdapter.fetch(path);
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
            File file = new File(baseDir, name);
            String path = file.getAbsolutePath();
            if (action == TransactionElement.ACTION_UPDATE) {
                fileAdapter.save(path, content);
            }
            if (action == TransactionElement.ACTION_REMOVE) {
                fileAdapter.remove(path);
            }
        }
    }
}