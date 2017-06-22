package com.ironz.binaryprefs.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Simple lock factory for providing lock by instance
 */
public final class SimpleLockFactoryImpl implements LockFactory {

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    @Override
    public Lock getReadLock() {
        return lock.readLock();
    }

    @Override
    public Lock getWriteLock() {
        return lock.writeLock();
    }
}