package com.ironz.binaryprefs.lock;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple lock factory for providing locks by name
 */
public final class SimpleLockFactoryImpl implements LockFactory {

    private final Map<String, Object> locks = new HashMap<>();
    private final String name;

    /**
     * Base constructor
     *
     * @param name given preferences set name
     */
    public SimpleLockFactoryImpl(String name) {
        this.name = name;
    }

    @Override
    public Object get() {
        if (!locks.containsKey(name)) {
            final Object lock = new Object();
            locks.put(name, lock);
            return lock;
        }
        return locks.get(name);
    }
}