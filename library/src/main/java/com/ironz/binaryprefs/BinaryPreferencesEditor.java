package com.ironz.binaryprefs;

import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.serialization.Bits;
import com.ironz.binaryprefs.serialization.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
final class BinaryPreferencesEditor implements PreferencesEditor {

    public static final String COMMIT_METHOD_KEY = "commit method";
    public static final String APPLY_METHOD_KEY = "apply method";

    private final List<Pair<String, byte[]>> commitList = new ArrayList<>(0);
    private final List<String> removeSet = new ArrayList<>(0);

    private final Preferences preferences;
    private final ExceptionHandler exceptionHandler;
    private final FileAdapter fileAdapter;
    private final EventBridge bridge;
    private final TaskExecutor taskExecutor;
    private final Class lock;

    private boolean clearFlag;

    BinaryPreferencesEditor(Preferences preferences,
                            FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            EventBridge bridge,
                            TaskExecutor taskExecutor,
                            Class lock) {
        this.preferences = preferences;
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.bridge = bridge;
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
            taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        clearInternal();
                        removeAll();
                        store();
                    } catch (Exception e) {
                        exceptionHandler.handle(e, APPLY_METHOD_KEY);
                    }
                }
            });
        }
    }

    @Override
    public boolean commit() {
        synchronized (lock) {
            try {
                clearInternal();
                removeAll();
                store();
                return true;
            } catch (Exception e) {
                exceptionHandler.handle(e, COMMIT_METHOD_KEY);
            }
            return false;
        }
    }

    private void clearInternal() {
        if (!clearFlag) {
            return;
        }
        for (String name : fileAdapter.names()) {
            removeInternal(name);
        }
    }

    private void removeAll() {
        for (final String name : removeSet) {
            removeInternal(name);
        }
    }

    private void store() {
        for (final Pair<String, byte[]> pair : commitList) {
            final String name = pair.getFirst();
            storeInternal(name, pair.getSecond());
        }
    }

    private void removeInternal(String name) {
        fileAdapter.remove(name);
        bridge.notifyListenersRemove(preferences, name);
    }

    private void storeInternal(String name, byte[] value) {
        fileAdapter.save(name, value);
        bridge.notifyListenersUpdate(preferences, name, value);
    }
}