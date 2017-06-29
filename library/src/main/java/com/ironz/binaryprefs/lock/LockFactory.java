package com.ironz.binaryprefs.lock;

import java.util.concurrent.locks.Lock;

/**
 * Describes algorithm for fetching locks
 */
public interface LockFactory {
    /**
     * Returns read lock object for synchronous operations.
     *
     * @param name given preferences name
     * @return lock object, instantiated for concrete preference
     */
    Lock getReadLock(String name);

    /**
     * Returns write lock object for synchronous operations.
     *
     * @param name given preferences name
     * @return lock object, instantiated for concrete preference
     */
    Lock getWriteLock(String name);
}