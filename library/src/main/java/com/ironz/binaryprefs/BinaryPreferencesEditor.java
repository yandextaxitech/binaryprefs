package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.files.FileAdapter;
import com.ironz.binaryprefs.util.Bits;
import com.ironz.binaryprefs.util.Constants;

import java.util.*;

final class BinaryPreferencesEditor implements SharedPreferences.Editor {

    private final Map<String, byte[]> commitMap = new HashMap<>();
    private final Set<String> removeSet = new HashSet<>();
    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final List<SharedPreferences.OnSharedPreferenceChangeListener> listeners;
    private final SharedPreferences preferences;

    private boolean clear;

    BinaryPreferencesEditor(FileAdapter fileAdapter,
                            ExceptionHandler exceptionHandler,
                            List<SharedPreferences.OnSharedPreferenceChangeListener> listeners,
                            SharedPreferences preferences) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.listeners = listeners;
        this.preferences = preferences;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        if (value == null) {
            return remove(key);
        }
        byte[] bytes = value.getBytes();
        commitMap.put(key + Constants.STRING_FILE_POSTFIX, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        if (values == null) {
            return remove(key);
        }
        int i = 0;
        for (String value : values) {
            String name = key + "." + i + Constants.STRING_SET_FILE_POSTFIX;
            commitMap.put(name, value.getBytes());
            i++;
        }
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        commitMap.put(key + Constants.INTEGER_FILE_POSTFIX, Bits.intToBytes(value));
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        byte[] bytes = Bits.longToBytes(value);
        commitMap.put(key + Constants.LONG_FILE_POSTFIX, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        byte[] bytes = Bits.floatToBytes(value);
        commitMap.put(key + Constants.FLOAT_FILE_POSTFIX, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        byte[] bytes = Bits.booleanToBytes(value);
        commitMap.put(key + Constants.BOOLEAN_FILE_POSTFIX, bytes);
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
        performStore();
    }

    @Override
    public boolean commit() {
        try {
            performStore();
            return true;
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return false;
    }

    private void performStore() {
        if (clear) {
            fileAdapter.clear();
        }
        for (String s : removeSet) {
            fileAdapter.remove(s);
            notifyListeners(s);
        }
        for (String key : commitMap.keySet()) {
            fileAdapter.save(key, commitMap.get(key));
            notifyListeners(key);
        }
    }

    private void notifyListeners(String key) {
        for (SharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
            listener.onSharedPreferenceChanged(preferences, key.split("\\.", 2)[0]);
        }
    }
}