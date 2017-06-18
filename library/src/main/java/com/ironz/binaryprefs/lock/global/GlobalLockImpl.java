package com.ironz.binaryprefs.lock.global;

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
        } catch (Exception ignored) {
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            lock = channel.lock();
        } catch (Exception e) {
            throw new InterruptedException(e.getMessage());
        }
    }

    @Override
    public boolean tryLock() {
        try {
            lock = channel.tryLock();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock();
    }

    @Override
    public void unlock() {
        try {
            lock.release();
        } catch (Exception ignored) {
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("New condition does not supported!");
    }
}