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
    public TransactionElement[] fetch() {
        try {
            FileTransactionElement[] fileTransactionElements = transactionBridge.fetch();
            TransactionElement[] elements = new TransactionElement[fileTransactionElements.length];
            for (int i = 0; i < fileTransactionElements.length; i++) {
                FileTransactionElement element = fileTransactionElements[i];
                String name = element.getName();
                byte[] content = element.getContent();
                elements[i] = new TransactionElement(TransactionElement.ACTION_FETCH, name, content);
            }
            return elements;
        } catch (RemoteException e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public boolean commit(TransactionElement[] elements) {
        try {
            FileTransactionElement[] fileTransactionElements = new FileTransactionElement[elements.length];
            for (int i = 0; i < elements.length; i++) {
                TransactionElement element = elements[i];
                int action = element.getAction();
                String name = element.getName();
                byte[] content = element.getContent();
                fileTransactionElements[i] = new FileTransactionElement(action, name, content);
            }
            return transactionBridge.commit(fileTransactionElements);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }
}