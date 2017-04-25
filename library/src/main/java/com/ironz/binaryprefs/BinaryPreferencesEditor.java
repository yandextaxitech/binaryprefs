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
            String name = keyNameProvider.convertStringName(key);
            byte[] bytes = value.getBytes();
            commitList.add(new Pair<>(name, bytes));
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
                String name = keyNameProvider.convertStringSetName(key, i);
                byte[] bytes = value.getBytes();
                commitList.add(new Pair<>(name, bytes));
                i++;
            }
            return this;
        }
    }

    @Override
    public PreferencesEditor putInt(String key, int value) {
        synchronized (lock) {
            String name = keyNameProvider.convertIntName(key);
            byte[] bytes = Bits.intToBytes(value);
            commitList.add(new Pair<>(name, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putLong(String key, long value) {
        synchronized (lock) {
            String name = keyNameProvider.convertLongName(key);
            byte[] bytes = Bits.longToBytes(value);
            commitList.add(new Pair<>(name, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putFloat(String key, float value) {
        synchronized (lock) {
            String name = keyNameProvider.convertFloatName(key);
            byte[] bytes = Bits.floatToBytes(value);
            commitList.add(new Pair<>(name, bytes));
            return this;
        }
    }

    @Override
    public PreferencesEditor putBoolean(String key, boolean value) {
        synchronized (lock) {
            String name = keyNameProvider.convertBooleanName(key);
            byte[] bytes = Bits.booleanToBytes(value);
            commitList.add(new Pair<>(name, bytes));
            return this;
        }
    }

    @Override
    public <T extends Externalizable> PreferencesEditor putObject(String key, T value) {
        return this;
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
            String key = keyNameProvider.getKeyFromFileName(fileName);
            if (!removeSet.contains(key)) {
                continue;
            }
            fileAdapter.remove(fileName);
            notifyListeners(key);
        }
    }

    private void tryStoreByKeys() {
        for (Pair<String, byte[]> pair : commitList) {
            String name = pair.getFirst();
            byte[] value = pair.getSecond();
            String key = keyNameProvider.getKeyFromFileName(name);
            fileAdapter.save(name, value);
            notifyListeners(key);
        }
    }

    private void notifyListeners(String key) {
        for (SharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(preferences, key);
        }
    }
}