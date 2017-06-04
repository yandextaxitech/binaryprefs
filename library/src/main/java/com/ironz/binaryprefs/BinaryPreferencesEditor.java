package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.serialization.Bits;
import com.ironz.binaryprefs.serialization.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

final class BinaryPreferencesEditor implements PreferencesEditor {

    private final List<Pair<String, byte[]>> commitList = new ArrayList<>(0);
    private final List<String> removeSet = new ArrayList<>(0);

    private final Preferences preferences;
    private final ExceptionHandler exceptionHandler;
    private final FileAdapter fileAdapter;
    private final EventBridge bridge;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final Class lock;

    private boolean clearFlag;

    BinaryPreferencesEditor(Preferences preferences,
                            FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            EventBridge bridge,
                            CacheProvider cacheProvider,
                            TaskExecutor taskExecutor,
                            Class lock) {
        this.preferences = preferences;
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.bridge = bridge;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        this.lock = lock;
    }

    @Override
    public PreferencesEditor putString(String key, String value) {
        synchronized (lock) {
            if (value == null) {
                return remove(key);
            }
            byte[] bytes = Bits.stringToBytesWithFlag(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putStringSet(String key, Set<String> values) {
        synchronized (lock) {
            if (values == null) {
                return remove(key);
            }
            values.remove(null);
            byte[] bytes = Bits.stringSetToBytesWithFlag(values);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putInt(String key, int value) {
        synchronized (lock) {
            byte[] bytes = Bits.intToBytesWithFlag(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putLong(String key, long value) {
        synchronized (lock) {
            byte[] bytes = Bits.longToBytesWithFlag(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putFloat(String key, float value) {
        synchronized (lock) {
            byte[] bytes = Bits.floatToBytesWithFlag(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putBoolean(String key, boolean value) {
        synchronized (lock) {
            byte[] bytes = Bits.booleanToBytesWithFlag(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public <T extends Persistable> PreferencesEditor putPersistable(String key, T value) {
        synchronized (lock) {
            byte[] bytes = Bits.persistableToBytes(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor remove(String key) {
        synchronized (lock) {
            removeSet.add(key);
            return this;
        }
    }

    @Override
    public PreferencesEditor clear() {
        synchronized (lock) {
            clearFlag = true;
            return this;
        }
    }

    @Override
    public void apply() {
        synchronized (lock) {
            performTransactions();
        }
    }

    @Override
    public boolean commit() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private void performTransactions() {
        try {
            tryClearAll();
            tryRemoveByKeys();
            tryStoreByKeys();
        } catch (Exception e) {
            exceptionHandler.handle(e, "apply method");
        }
    }

    private void tryClearAll() {
        if (!clearFlag) {
            return;
        }
        for (final String name : fileAdapter.names()) {
            removeOne(name);
        }
    }

    private void tryRemoveByKeys() {
        for (final String name : removeSet) {
            removeOne(name);
        }
    }

    private void tryStoreByKeys() {
        for (final Pair<String, byte[]> pair : commitList) {
            storeOne(pair.getFirst(), pair.getSecond());
        }
    }

    private void removeOne(final String name) {
        taskExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return removeAndReturnName(name);
            }
        });
    }

    private void storeOne(final String name, final byte[] value) {
        taskExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return saveAndReturnName(name, value);
            }
        });
    }

    private String removeAndReturnName(String name) {
        cacheProvider.remove(name);
        fileAdapter.remove(name);
        bridge.notifyListenersRemove(preferences, name);
        return name;
    }

    private String saveAndReturnName(String name, byte[] value) {
        cacheProvider.put(name, value);
        fileAdapter.save(name, value);
        bridge.notifyListenersUpdate(preferences, name, value);
        return name;
    }
}