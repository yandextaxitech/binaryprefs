package com.ironz.binaryprefs.file.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.ironz.binaryprefs.exception.FileOperationException;

public final class MultiProcessTransaction implements FileTransaction {

    private final String baseDir;
    private FileTransactionBridge transactionBridge;

    public MultiProcessTransaction(Context context, String baseDir) {
        this.baseDir = baseDir;
        init(context);
    }

    private void init(Context context) {
        BlockingServiceBinder serviceBinder = new BlockingServiceBinder(context);
        Intent intent = new Intent(context, FileTransactionService.class);
        intent.putExtra(FileTransactionService.BASE_DIRECTORY, baseDir);
        IBinder iBinder = serviceBinder.bindService(intent);
        transactionBridge = FileTransactionBridge.Stub.asInterface(iBinder);
    }

    @Override
    public String[] names() {
        try {
            return transactionBridge.names();
        } catch (RemoteException e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public byte[] fetch(String name) {
        try {
            return transactionBridge.fetch(name);
        } catch (RemoteException e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public void apply(TransactionElement[] elements) {
        try {
            FileTransactionElement[] fileTransactionElements = new FileTransactionElement[elements.length];
            for (int i = 0; i < elements.length; i++) {
                TransactionElement element = elements[i];
                fileTransactionElements[i] = transform(element);
            }
            transactionBridge.apply(fileTransactionElements);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public boolean commit(TransactionElement[] elements) {
        try {
            FileTransactionElement[] fileTransactionElements = new FileTransactionElement[elements.length];
            for (int i = 0; i < elements.length; i++) {
                TransactionElement element = elements[i];
                fileTransactionElements[i] = transform(element);
            }
            return transactionBridge.commit(fileTransactionElements);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    private FileTransactionElement transform(TransactionElement element) {
        int action = element.getAction();
        String name = element.getName();
        byte[] content = element.getContent();
        return new FileTransactionElement(action, name, content);
    }
}