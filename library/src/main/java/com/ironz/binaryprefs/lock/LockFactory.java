package com.ironz.binaryprefs.lock;

/**
 * Describes algorithm for fetching locks
 */
public interface LockFactory {
    /**
     * Returns lock object for synchronous operations.
     *
     * @param name given preferences name
     * @return lock object, instantiated for concrete preference
     */
    Object get(String name);
}