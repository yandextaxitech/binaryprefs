package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;

public final class MultiProcessTransaction implements FileTransaction {

    private final FileAdapter fileAdapter;
    private final Lock lock;
    private final KeyEncryption keyEncryption;
    private final ValueEncryption valueEncryption;

    public MultiProcessTransaction(FileAdapter fileAdapter,
                                   LockFactory lockFactory,
                                   KeyEncryption keyEncryption,
                                   ValueEncryption valueEncryption) {
        this.fileAdapter = fileAdapter;
        this.lock = lockFactory.getProcessLock();
        this.valueEncryption = valueEncryption;
        this.keyEncryption = keyEncryption;
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public List<TransactionElement> fetchAll() {
        lock.lock();
        try {
            return fetchAllInternal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<String> fetchNames() {
        lock.lock();
        try {
            return fetchNamesInternal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TransactionElement fetchOne(String name) {
        lock.lock();
        try {
            return fetchOneInternal(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void commit(List<TransactionElement> elements) {
        lock.lock();
        try {
            commitInternal(elements);
        } finally {
            lock.unlock();
        }
    }

    private List<TransactionElement> fetchAllInternal() {
        String[] names = fileAdapter.names();
        List<TransactionElement> elements = new ArrayList<>(names.length);
        for (String name : names) {
            TransactionElement element = fetchOneInternal(name);
            elements.add(element);
        }
        return elements;
    }

    private HashSet<String> fetchNamesInternal() {
        String[] names = fileAdapter.names();
        List<String> list = Arrays.asList(names);
        return new HashSet<>(list);
    }

    private TransactionElement fetchOneInternal(String name) {
        byte[] bytes = fileAdapter.fetch(name);
        String encryptName = keyEncryption.decrypt(name);
        byte[] decrypt = valueEncryption.decrypt(bytes);
        return TransactionElement.createFetchElement(encryptName, decrypt);
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