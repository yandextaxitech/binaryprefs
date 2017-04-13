package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.files.FileAdapter;
import com.ironz.binaryprefs.task.TaskHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements SharedPreferences {

    private final TaskHandler taskHandler;
    private final FileAdapter fileAdapter;

    BinaryPreferences(FileAdapter fileAdapter) {
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
            byte[] bytes = fileAdapter.fetch(key + Constants.STRING_FILE_POSTFIX);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            final HashSet<String> strings = new HashSet<>();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                String name = key + "." + i + Constants.STRING_SET_FILE_POSTFIX;
                if (fileAdapter.contains(name)) {
                    byte[] bytes = fileAdapter.fetch(name);
                    strings.add(new String(bytes));
                    continue;
                }
                break;
            }
            return strings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.INTEGER_FILE_POSTFIX);
            return Bits.intFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.LONG_FILE_POSTFIX);
            return Bits.longFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.FLOAT_FILE_POSTFIX);
            int i = Bits.intFromBytes(bytes);
            return Float.intBitsToFloat(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.BOOLEAN_FILE_POSTFIX);
            return Bits.booleanFromBytes(bytes);
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
