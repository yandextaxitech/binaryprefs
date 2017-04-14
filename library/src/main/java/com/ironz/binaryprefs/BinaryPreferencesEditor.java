package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.files.FileAdapter;
import com.ironz.binaryprefs.name.KeyNameProvider;
import com.ironz.binaryprefs.util.Bits;

import java.util.*;

final class BinaryPreferencesEditor implements SharedPreferences.Editor {

    private final Map<String, byte[]> commitMap = new HashMap<>();
    private final Set<String> removeSet = new HashSet<>();
    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final List<SharedPreferences.OnSharedPreferenceChangeListener> listeners;
    private final SharedPreferences preferences;
    private final KeyNameProvider keyNameProvider;

    private boolean clear;

    BinaryPreferencesEditor(FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            List<SharedPreferences.OnSharedPreferenceChangeListener> listeners,
                            SharedPreferences preferences,
                            KeyNameProvider keyNameProvider) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.listeners = listeners;
        this.preferences = preferences;
        this.keyNameProvider = keyNameProvider;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        if (value == null) {
            return remove(key);
        }
        String name = keyNameProvider.convertStringName(key);
        byte[] bytes = value.getBytes();
        commitMap.put(name, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        if (values == null) {
            return remove(key);
        }
        int i = 0;
        for (String value : values) {
            String name = keyNameProvider.convertStringSetName(key, i);
            byte[] bytes = value.getBytes();
            commitMap.put(name, bytes);
            i++;
        }
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        String name = keyNameProvider.convertIntName(key);
        byte[] bytes = Bits.intToBytes(value);
        commitMap.put(name, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        String name = keyNameProvider.convertLongName(key);
        byte[] bytes = Bits.longToBytes(value);
        commitMap.put(name, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        String name = keyNameProvider.convertFloatName(key);
        byte[] bytes = Bits.floatToBytes(value);
        commitMap.put(name, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        String name = keyNameProvider.convertBooleanName(key);
        byte[] bytes = Bits.booleanToBytes(value);
        commitMap.put(name, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        removeSet.add(key);
        return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
        clear = true;
        return this;
    }

    @Override
    public void apply() {
        performActions();
    }

    @Override
    public boolean commit() {
        return withCallBack();
    }

    private boolean withCallBack() {
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
            String key = getKeyFromFileName(fileName);
            if (!removeSet.contains(key)) {
                continue;
            }
            fileAdapter.remove(fileName);
            notifyListeners(key);
        }
    }

    private void tryStoreByKeys() {
        for (String fileName : commitMap.keySet()) {
            String key = getKeyFromFileName(fileName);
            fileAdapter.save(fileName, commitMap.get(fileName));
            notifyListeners(key);
        }
    }

    private void notifyListeners(String key) {
        for (SharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(preferences, key);
        }
    }

    private String getKeyFromFileName(String fileName) {
        return fileName.split("\\.", 2)[0];
    }
}