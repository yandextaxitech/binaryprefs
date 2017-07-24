package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

@SuppressWarnings("unused")
public final class MultiProcessTransactionImpl implements FileTransaction {

    private final FileAdapter fileAdapter;
    private final LockFactory lockFactory;
    private final ValueEncryption valueEncryption;
    private final KeyEncryption keyEncryption;

    public MultiProcessTransactionImpl(FileAdapter fileAdapter,
                                       LockFactory lockFactory,
                                       ValueEncryption valueEncryption,
                                       KeyEncryption keyEncryption) {
        this.fileAdapter = fileAdapter;
        this.lockFactory = lockFactory;
        this.valueEncryption = valueEncryption;
        this.keyEncryption = keyEncryption;
    }

    @Override
    public List<TransactionElement> fetch() {
        Lock lock = lockFactory.getProcessLock();
        lock.lock();
        try {
            return fetchInternal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void commit(List<TransactionElement> elements) {
        Lock lock = lockFactory.getProcessLock();
        lock.lock();
        try {
            commitInternal(elements);
        } finally {
            lock.unlock();
        }
    }

    private List<TransactionElement> fetchInternal() {
        String[] names = fileAdapter.names();
        List<TransactionElement> elements = new ArrayList<>(names.length);
        for (String name : names) {
            byte[] bytes = fileAdapter.fetch(name);
            String encryptName = keyEncryption.decrypt(name);
            byte[] decrypt = valueEncryption.decrypt(bytes);
            TransactionElement element = TransactionElement.createFetchElement(encryptName, decrypt);
            elements.add(element);
        }
        return elements;
    }

    private void commitInternal(List<TransactionElement> elements) {
        for (TransactionElement element : elements) {
            int action = element.getAction();
            String name = element.getName();
            String encryptedName = keyEncryption.encrypt(name);
            byte[] content = element.getContent();
            byte[] encrypt = valueEncryption.encrypt(content);
            if (action == TransactionElement.ACTION_UPDATE) {
                fileAdapter.save(encryptedName, encrypt);
            }
            if (action == TransactionElement.ACTION_REMOVE) {
                fileAdapter.remove(encryptedName);
            }
        }
    }
}