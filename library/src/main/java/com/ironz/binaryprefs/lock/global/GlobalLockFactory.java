package com.ironz.binaryprefs.lock.global;

import java.util.concurrent.locks.Lock;

/**
 * Describes algorithm for fetching global locks for IPC mechanism.
 */
public interface GlobalLockFactory {
    /**
     * Returns global lock object for IPC synchronous operations.
     *
     * @return target lock object
     */
    Lock getLock();
}
