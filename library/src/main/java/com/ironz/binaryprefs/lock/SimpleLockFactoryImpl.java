package com.ironz.binaryprefs.lock;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple lock factory for providing locks by name
 */
public final class SimpleLockFactoryImpl implements LockFactory {

    private final Map<String, Object> locks = new HashMap<>();

    @Override
    public Object get(String name) {
        if (locks.containsKey(name)) {
            return locks.get(name);
        }
        final Object lock = new Object();
        locks.put(name, lock);
        return lock;
    }
}