package com.ironz.binaryprefs.lock;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.lock.global.FileGlobalLockFactoryImpl;
import com.ironz.binaryprefs.lock.global.GlobalLockFactory;

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
    private GlobalLockFactory globalLockFactory;

    public SimpleLockFactoryImpl(DirectoryProvider directoryProvider, ExceptionHandler exceptionHandler) {
        this.globalLockFactory = new FileGlobalLockFactoryImpl(directoryProvider, exceptionHandler);
    }

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

    @Override
    public GlobalLockFactory getGlobalLockFactory() {
        return globalLockFactory;
    }
}