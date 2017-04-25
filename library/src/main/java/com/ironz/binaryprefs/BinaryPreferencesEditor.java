package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.name.KeyNameProvider;
import com.ironz.binaryprefs.util.Bits;
import com.ironz.binaryprefs.util.Pair;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class BinaryPreferencesEditor implements PreferencesEditor {

    private final List<Pair<String, byte[]>> commitList = new ArrayList<>(0);
    private final List<Pair<Pair<String, String>, byte[]>> setCommitList = new ArrayList<>(0);
    private final List<String> removeSet = new ArrayList<>(0);

    private final Class lock;
    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final List<SharedPreferences.OnSharedPreferenceChangeListener> listeners;
    private final SharedPreferences preferences;
    private final KeyNameProvider keyNameProvider;

    private boolean clear;

    BinaryPreferencesEditor(Class lock,
                            FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            List<SharedPreferences.OnSharedPreferenceChangeListener> listeners,
                            SharedPreferences preferences,
                            KeyNameProvider keyNameProvider) {
        this.lock = lock;
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.listeners = listeners;
        this.preferences = preferences;
        this.keyNameProvider = keyNameProvider;
    }

    @Override
    public PreferencesEditor putString(String key, String value) {
        synchronized (lock) {
            if (value == null) {
                return remove(key);
            }
            byte[] bytes = value.getBytes();
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
            int i = 0;
            for (String value : values) {
                byte[] bytes = Bits.stringToBytes(value);
                setCommitList.add(new Pair<>(
                        new Pair<>(key, String.valueOf(i)),
                        bytes)
                );
                i++;
            }
            return this;
        }
    }

    @Override
    public PreferencesEditor putInt(String key, int value) {
        synchronized (lock) {
            byte[] bytes = Bits.intToBytes(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putLong(String key, long value) {
        synchronized (lock) {
            byte[] bytes = Bits.longToBytes(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putFloat(String key, float value) {
        synchronized (lock) {
            byte[] bytes = Bits.floatToBytes(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putBoolean(String key, boolean value) {
        synchronized (lock) {
            byte[] bytes = Bits.booleanToBytes(value);
            commitList.add(new Pair<>(key, bytes));
            return this;
        }
    }

    @Override
    public <T extends Externalizable> PreferencesEditor putObject(String key, T value) {
        throw new UnsupportedOperationException("Not implemented yet");
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
            performActions();
        }
    }

    @Override
    public boolean commit() {
        synchronized (lock) {
            return performWithResult();
        }
    }

    private boolean performWithResult() {
        try {
            performActions();
            return true;
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return false;
    }

    private void performActions() {
        tryClearAll();
        tryRemoveByKeys();
        tryStoreByKeys();
    }

    private void tryClearAll() {
        if (clear) {
            fileAdapter.clear();
        }
    }

    private void tryRemoveByKeys() {
        for (String fileName : fileAdapter.names()) {
            if (!removeSet.contains(fileName)) {
                continue;
            }
            fileAdapter.remove(fileName);
            notifyListeners(fileName);
        }
    }

    private void tryStoreByKeys() {
        for (Pair<String, byte[]> pair : commitList) {
            String name = pair.getFirst();
            byte[] value = pair.getSecond();
            fileAdapter.save(name, value);
            notifyListeners(name);
        }
        for (Pair<Pair<String, String>, byte[]> pairPair : setCommitList) {
            Pair<String, String> name = pairPair.getFirst();
            byte[] value = pairPair.getSecond();
            fileAdapter.save(name.getFirst(), name.getSecond(), value);
            notifyListeners(name.getFirst());
        }
    }

    private void notifyListeners(String key) {
        for (SharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(preferences, key);
        }
    }
}