package com.ironz.binaryprefs.file.transaction;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class FileTransactionService extends Service {

    public static final String DEFAULT_FILE_DIRECTORY = "DEFAULT_FILE_DIRECTORY";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return createBinder();
    }

    private IBinder createBinder() {
        return new FileTransactionBridge.Stub() {
            @Override
            public boolean commit(ParcelableFileTransactionElement[] elements) throws RemoteException {
                return false;
            }

            @Override
            public void apply(ParcelableFileTransactionElement[] elements) throws RemoteException {

            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}