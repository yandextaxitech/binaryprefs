package com.ironz.binaryprefs.lock;

import java.util.concurrent.locks.Lock;

/**
 * Describes algorithm for fetching locks
 */
public interface LockFactory {
    /**
     * Returns read lock object for synchronous operations.
     *
     * @return lock object, instantiated for concrete preference
     */
    Lock getReadLock();

    /**
     * Returns write lock object for synchronous operations.
     *
     * @return lock object, instantiated for concrete preference
     */
    Lock getWriteLock();

    /**
     * Returns VM lock for synchronous file operations.
     *
     * @return lock object, instantiated for concrete preference
     */
    Lock getProcessLock();
}