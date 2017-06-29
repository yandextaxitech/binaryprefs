package com.ironz.binaryprefs.lock;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class GlobalFileLock implements Lock {

    private static final String RWD_MODE = "rwd";

    private final File lockFile;

    private FileLock lock;
    private FileChannel channel;

    GlobalFileLock(File lockFile) {
        this.lockFile = lockFile;
        initRandomAccessFile();
    }

    private void initRandomAccessFile() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, RWD_MODE);
            channel = randomAccessFile.getChannel();
            if (!lockFile.exists()) {
                randomAccessFile.write(0);
            }
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
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
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Not implemented!");
    }
}