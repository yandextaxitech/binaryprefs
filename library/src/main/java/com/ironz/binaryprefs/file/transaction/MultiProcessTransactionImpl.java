package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

@SuppressWarnings("unused")
public final class MultiProcessTransactionImpl implements FileTransaction {

    private static final String COMMIT = "commit";

    private final FileAdapter fileAdapter;
    private final LockFactory lockFactory;

    public MultiProcessTransactionImpl(FileAdapter fileAdapter, LockFactory lockFactory) {
        this.fileAdapter = fileAdapter;
        this.lockFactory = lockFactory;
    }

    @Override
    public List<TransactionElement> fetch() {
        Lock lock = lockFactory.getProcessLock();
        lock.lock();
        try {
            String[] names = fileAdapter.names();
            List<TransactionElement> elements = new ArrayList<>();
            for (String name : names) {
                byte[] bytes = fileAdapter.fetch(name);
                TransactionElement element = TransactionElement.createFetchElement(name, bytes);
                elements.add(element);
            }
            return elements;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void commit(List<TransactionElement> elements) {
        Lock lock = lockFactory.getProcessLock();
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }
}