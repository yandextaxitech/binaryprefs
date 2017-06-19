package com.ironz.binaryprefs.lock.global;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class GlobalLockImpl implements Lock {

    private final FileChannel channel;
    private FileLock lock;

    public GlobalLockImpl(FileChannel channel) {
        this.channel = channel;
    }

    @Override
    public void lock() {
        try {
            lock = channel.lock();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public void unlock() {
        try {
            lock.release();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Not supported!");
    }
}