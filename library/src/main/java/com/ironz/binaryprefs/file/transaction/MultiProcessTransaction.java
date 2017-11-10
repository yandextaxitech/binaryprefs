package com.ironz.binaryprefs.file.transaction;

import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        return fetchAllInternal();
    }

    @Override
    public Set<String> fetchNames() {
        return fetchNamesInternal();
    }

    @Override
    public TransactionElement fetchOne(String name) {
        return fetchOneInternal(name);
    }

    @Override
    public void commit(List<TransactionElement> elements) {
        commitInternal(elements);
    }

    private List<TransactionElement> fetchAllInternal() {
        String[] names = fileAdapter.names();
        List<TransactionElement> elements = new ArrayList<>(names.length);
        for (String name : names) {
            String decryptedName = keyEncryption.decrypt(name);
            TransactionElement element = fetchOneInternal(decryptedName);
            elements.add(element);
        }
        return elements;
    }

    private Set<String> fetchNamesInternal() {
        String[] names = fileAdapter.names();
        Set<String> temp = new HashSet<>();
        for (String name : names) {
            String decrypt = keyEncryption.decrypt(name);
            temp.add(decrypt);
        }
        return temp;
    }

    private TransactionElement fetchOneInternal(String decryptedName) {
        String encryptName = keyEncryption.encrypt(decryptedName);
        byte[] content = fileAdapter.fetch(encryptName);
        byte[] decryptValue = valueEncryption.decrypt(content);
        return TransactionElement.createFetchElement(decryptedName, decryptValue);
    }

    private void commitInternal(List<TransactionElement> elements) {
        for (TransactionElement element : elements) {
            int action = element.getAction();
            String name = element.getName();
            String encryptedName = keyEncryption.encrypt(name);
            if (action == TransactionElement.ACTION_UPDATE) {
                byte[] value = element.getContent();
                byte[] encryptedValue = valueEncryption.encrypt(value);
                fileAdapter.save(encryptedName, encryptedValue);
            }
            if (action == TransactionElement.ACTION_REMOVE) {
                fileAdapter.remove(encryptedName);
            }
        }
    }
}