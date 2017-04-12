package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.files.FileAdapter;
import com.ironz.binaryprefs.task.TaskHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements SharedPreferences {

    private final TaskHandler taskHandler;
    private final FileAdapter fileAdapter;

    public BinaryPreferences(FileAdapter fileAdapter) {
        taskHandler = new TaskHandler(fileAdapter);
        this.fileAdapter = fileAdapter;
    }

    @Override
    public Map<String, ?> getAll() {
        return new HashMap<>();
    }

    @Override
    public String getString(String key, String defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + ".s");
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + ".i");
            return getIntFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + "l");
            return getLongFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + ".f");
            int i = getIntFromBytes(bytes);
            return Float.intBitsToFloat(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + ".b");
            return getBooleanFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        for (String s : fileAdapter.names()) {
            if (s.split("\\.")[0].equals(key)) {
                return true;
            }
        }
        return false;
    }

    private int getIntFromBytes(byte[] b) {
        int i = 0xFF;
        return ((b[3] & i)) +
                ((b[2] & i) << 8) +
                ((b[1] & i) << 16) +
                ((b[0]) << 24);
    }

    private long getLongFromBytes(byte[] b) {
        long l = 0xFFL;
        return ((b[7] & l)) +
                ((b[6] & l) << 8) +
                ((b[5] & l) << 16) +
                ((b[4] & l) << 24) +
                ((b[3] & l) << 32) +
                ((b[2] & l) << 40) +
                ((b[1] & l) << 48) +
                (((long) b[0]) << 56);
    }

    private boolean getBooleanFromBytes(byte[] bytes) {
        return bytes[0] != 0;
    }

    @Override
    public Editor edit() {
        return new BinaryPreferencesEditor(taskHandler);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
