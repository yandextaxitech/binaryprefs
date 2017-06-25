package com.ironz.binaryprefs.file.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.ironz.binaryprefs.exception.FileOperationException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class BlockingServiceBinder {

    private static final String TAG = "ServiceTestRule";
    private static IBinder mIBinder;

    private final Context context;
    private ServiceConnection serviceConnection;

    private long timeoutValue = 5;
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;

    public BlockingServiceBinder(Context context) {
        this.context = context;
    }

    public IBinder bindService(Intent intent) {
        bindServiceAndWait(intent, Context.BIND_AUTO_CREATE);
        return mIBinder;
    }

    private void bindServiceAndWait(Intent intent, int flags) {

        serviceConnection = new ProxyServiceConnection();

        boolean isBound = context.bindService(intent, serviceConnection, flags);

        if (!isBound) {
            throw new FileOperationException("Can't establish connection with transaction service");
        }
        waitOnLatch(ProxyServiceConnection.countDownLatch);
    }

    private void waitOnLatch(CountDownLatch latch) {
        try {
            if (!latch.await(timeoutValue, timeoutUnit)) {
                throw new FileOperationException("Waited for " + timeoutValue + " " + timeoutUnit.name() + ", but service was never connected");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for service to be connected", e);
        }
    }

    private static class ProxyServiceConnection implements ServiceConnection {

        static CountDownLatch countDownLatch = new CountDownLatch(1);

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBinder = service;
            countDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIBinder = null;
        }
    }
}