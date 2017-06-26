package com.ironz.binaryprefs.file.transaction;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;

public class FileTransactionService extends Service {

    public static final String BASE_DIRECTORY = "base_directory";
    public static final String CAUGHT_EXCEPTION_EVENT = "caught_exception";
    public static final String EXCEPTION = "exception";

    private FileAdapter fileAdapter;
    private String baseDir;

    @Override
    public IBinder onBind(Intent intent) {
        baseDir = intent.getStringExtra(BASE_DIRECTORY);
        fileAdapter = new NioFileAdapter();
        return createBinder();
    }

    private IBinder createBinder() {
        return new FileTransactionBridge.Stub() {
            @Override
            public FileTransactionElement[] fetch() throws RemoteException {
                return fetchInternal();
            }

            @Override
            public boolean commit(FileTransactionElement[] elements) throws RemoteException {
                return commitBlocking(elements);
            }
        };
    }

    private FileTransactionElement[] fetchInternal() {
        synchronized (FileTransactionService.class) {
            String[] names = fileAdapter.names(baseDir);
            FileTransactionElement[] elements = new FileTransactionElement[names.length];
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                byte[] bytes = fileAdapter.fetch(name);
                int action = FileTransactionElement.ACTION_FETCH;
                elements[i] = new FileTransactionElement(action, name, bytes);
            }
            return elements;
        }
    }

    private boolean commitBlocking(FileTransactionElement[] elements) {
        synchronized (FileTransactionService.class) {
            return commitInternal(elements);
        }
    }

    private boolean commitInternal(FileTransactionElement[] elements) {
        try {
            for (FileTransactionElement e : elements) {
                transactOne(e);
            }
            return true;
        } catch (Exception e) {
            sendExceptionToLogger(e);
        }
        return false;
    }

    private void transactOne(FileTransactionElement e) {

        int action = e.getAction();
        String name = e.getName();
        byte[] content = e.getContent();

        if (action == FileTransactionElement.ACTION_UPDATE) {
            fileAdapter.save(name, content);
        }

        if (action == FileTransactionElement.ACTION_REMOVE) {
            fileAdapter.remove(name);
        }
    }

    private void sendExceptionToLogger(Exception e) {
        Intent intent = new Intent(CAUGHT_EXCEPTION_EVENT);
        intent.putExtra(EXCEPTION, e);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}