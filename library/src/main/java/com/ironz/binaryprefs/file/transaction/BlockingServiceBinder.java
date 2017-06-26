package com.ironz.binaryprefs.file.transaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.ironz.binaryprefs.exception.FileOperationException;

final class BlockingServiceBinder {

    private final Context context;

    BlockingServiceBinder(Context context) {
        this.context = context;
    }

    IBinder bindService(Intent intent) {
        BlockingServiceConnection connection = new BlockingServiceConnection();
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        connection.waitUntilConnected();
        return connection.binder;
    }

    private static final class BlockingServiceConnection implements ServiceConnection {

        private boolean connected = false;
        private final Object lock = new Object();
        private IBinder binder;

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            connected = true;
            synchronized (lock) {
                this.binder = binder;
                lock.notifyAll();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }

        void waitUntilConnected() {
            if (!connected) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new FileOperationException(e);
                    }
                }
            }
        }
    }
}