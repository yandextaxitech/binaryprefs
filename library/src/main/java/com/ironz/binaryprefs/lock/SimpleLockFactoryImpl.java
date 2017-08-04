package com.ironz.binaryprefs.lock;

import com.ironz.binaryprefs.file.directory.DirectoryProvider;

import java.io.File;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Simple lock factory for providing lock by instance and global lock by preference name.
 */
public final class SimpleLockFactoryImpl implements LockFactory {

    private static final String LOCK_EXTENSION = ".lock";

    private final File lockDirectory;

    private final ReadWriteLock readWriteLock;
    private final Lock processLock;

    public SimpleLockFactoryImpl(String prefName,
                                 DirectoryProvider provider,
                                 Map<String, ReadWriteLock> locks,
                                 Map<String, Lock> processLocks) {
        this.lockDirectory = provider.getLockDirectory();
        this.readWriteLock = initLocalLock(prefName, locks);
        this.processLock = initProcessLock(prefName, processLocks);
    }

    private ReadWriteLock initLocalLock(String name, Map<String, ReadWriteLock> locks) {
        if (locks.containsKey(name)) {
            return locks.get(name);
        }
        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        locks.put(name, lock);
        return lock;
    }

    private Lock initProcessLock(String name, Map<String, Lock> processLocks) {
        if (processLocks.containsKey(name)) {
            return processLocks.get(name);
        }
        File file = new File(lockDirectory, name + LOCK_EXTENSION);
        Lock lock = new ProcessFileLock(file);
        processLocks.put(name, lock);
        return lock;
    }

    @Override
    public Lock getReadLock() {
        return readWriteLock.readLock();
    }

    @Override
    public Lock getWriteLock() {
        return readWriteLock.writeLock();
    }

    @Override
    public Lock getProcessLock() {
        return processLock;
    }
}