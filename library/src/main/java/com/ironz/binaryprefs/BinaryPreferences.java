package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.files.FileAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements SharedPreferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;

    BinaryPreferences(FileAdapter fileAdapter, ExceptionHandler exceptionHandler) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Map<String, ?> getAll() {
        Map<String, Object> map = new HashMap<>();
        try {
            for (String name : fileAdapter.names()) {

                String[] split = name.split("\\.");
                String suffix = split[split.length - 1];
                String prefName = split[0];

                if (suffix.equals(Constants.STRING_FILE_POSTFIX_WITHOUT_DOT)) {
                    map.put(prefName, new String(fileAdapter.fetch(name)));
                    continue;
                }
                if (suffix.equals(Constants.INTEGER_FILE_POSTFIX_WITHOUT_DOT)) {
                    map.put(prefName, Bits.intFromBytes(fileAdapter.fetch(name)));
                    continue;
                }
                if (suffix.equals(Constants.LONG_FILE_POSTFIX_WITHOUT_DOT)) {
                    map.put(prefName, Bits.longFromBytes(fileAdapter.fetch(name)));
                    continue;
                }
                if (suffix.equals(Constants.FLOAT_FILE_POSTFIX_WITHOUT_DOT)) {
                    map.put(prefName, Bits.floatFromBytes(fileAdapter.fetch(name)));
                    continue;
                }
                if (suffix.equals(Constants.BOOLEAN_FILE_POSTFIX_WITHOUT_DOT)) {
                    map.put(prefName, Bits.booleanFromBytes(fileAdapter.fetch(name)));
                    continue;
                }
                if (suffix.equals(Constants.STRING_SET_FILE_POSTFIX_WITHOUT_DOT)) {
                    map.put(prefName, getStrings(prefName));
                }
            }
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return map;
    }

    @Override
    public String getString(String key, String defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.STRING_FILE_POSTFIX);
            return new String(bytes);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            return getStrings(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValues;
    }

    private Set<String> getStrings(String key) {
        final HashSet<String> strings = new HashSet<>(0);
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
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.INTEGER_FILE_POSTFIX);
            return Bits.intFromBytes(bytes);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.LONG_FILE_POSTFIX);
            return Bits.longFromBytes(bytes);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.FLOAT_FILE_POSTFIX);
            return Bits.floatFromBytes(bytes);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            byte[] bytes = fileAdapter.fetch(key + Constants.BOOLEAN_FILE_POSTFIX);
            return Bits.booleanFromBytes(bytes);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        for (String s : fileAdapter.names()) {
            if (s.split("\\.", 2)[0].equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Editor edit() {
        return new BinaryPreferencesEditor(fileAdapter, exceptionHandler);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}