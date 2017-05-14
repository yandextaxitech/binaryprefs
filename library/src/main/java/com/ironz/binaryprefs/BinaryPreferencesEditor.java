package com.ironz.binaryprefs;

import com.ironz.binaryprefs.events.PreferenceEventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.util.Bits;
import com.ironz.binaryprefs.util.Pair;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class BinaryPreferencesEditor implements PreferencesEditor {

    private final List<Pair<String, byte[]>> commitList = new ArrayList<>(0);
    private final List<String> removeSet = new ArrayList<>(0);

    private final Preferences preferences;
    private final ExceptionHandler exceptionHandler;
    private final FileAdapter fileAdapter;
    private final PreferenceEventBridge bridge;
    private final Class lock;

    private boolean clear;

    BinaryPreferencesEditor(Preferences preferences,
                            FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            PreferenceEventBridge bridge,
                            Class lock) {
        this.lock = lock;
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.preferences = preferences;
        this.bridge = bridge;
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
    public <T extends Externalizable> PreferencesEditor putObject(String key, T value) {
        throw new UnsupportedOperationException("Not implemented yet!");
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
            clear = true;
            return this;
        }
    }

    @Override
    public void apply() {
        synchronized (lock) {
            performWithResult(); //result is ignored
        }
    }

    @Override
    public boolean commit() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private void performWithResult() {
        try {
            tryClearAll();
            tryRemoveByKeys();
            tryStoreByKeys();
        } catch (Exception e) {
            exceptionHandler.handle(e, "apply method");
        }
    }

    private void tryClearAll() {
        if (clear) {
            fileAdapter.clear();
        }
    }

    private void tryRemoveByKeys() {
        for (String name : removeSet) {
            fileAdapter.remove(name);
            notifyListeners(name);
        }
    }

    private void tryStoreByKeys() {
        for (Pair<String, byte[]> pair : commitList) {
            String name = pair.getFirst();
            byte[] value = pair.getSecond();
            fileAdapter.save(name, value);
            notifyListeners(name);
        }
    }

    private void notifyListeners(String key) {
        bridge.notifyListeners(preferences, key);
    }
}