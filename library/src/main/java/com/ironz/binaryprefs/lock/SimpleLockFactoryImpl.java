package com.ironz.binaryprefs.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Simple lock factory for providing locks by name
 */
public final class SimpleLockFactoryImpl implements LockFactory {

    private final Map<String, ReadWriteLock> locks = new HashMap<>();

    @Override
    public Lock getReadLock(String name) {
        if (locks.containsKey(name)) {
            ReadWriteLock readWriteLock = locks.get(name);
            return readWriteLock.readLock();
        }
        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        locks.put(name, lock);
        return lock.readLock();
    }

    @Override
    public Lock getWriteLock(String name) {
        if (locks.containsKey(name)) {
            ReadWriteLock readWriteLock = locks.get(name);
            return readWriteLock.writeLock();
        }
        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        locks.put(name, lock);
        return lock.writeLock();
    }
}