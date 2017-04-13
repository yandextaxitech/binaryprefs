package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.task.TaskHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BinaryPreferencesEditor implements SharedPreferences.Editor {

    private final Map<String, byte[]> commitMap = new HashMap<>();
    private final Set<String> removeSet = new HashSet<>();

    private final TaskHandler taskHandler;

    private boolean clear;

    BinaryPreferencesEditor(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        byte[] bytes = value.getBytes();
        commitMap.put(key + Constants.STRING_FILE_POSTFIX, bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
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
        taskHandler.apply(clear, removeSet, commitMap);
    }

    @Override
    public boolean commit() {
        return taskHandler.commit(clear, removeSet, commitMap);
    }
}