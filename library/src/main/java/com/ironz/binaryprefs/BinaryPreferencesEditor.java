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
        commitMap.put(key + ".s", bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        int i = 0;
        for (String value : values) {
            commitMap.put(key + ".ss." + i, value.getBytes());
            i++;
        }
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        commitMap.put(key + ".i", intToBytes(value));
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        byte[] bytes = longToBytes(value);
        commitMap.put(key + ".l", bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        byte[] bytes = floatToBytes(value);
        commitMap.put(key + ".f", bytes);
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        byte[] bytes = booleanToBytes(value);
        commitMap.put(key + ".b", bytes);
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

    private byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    private byte[] floatToBytes(float value) {
        int i = Float.floatToIntBits(value);
        return intToBytes(i);
    }

    private byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    private byte[] booleanToBytes(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
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
