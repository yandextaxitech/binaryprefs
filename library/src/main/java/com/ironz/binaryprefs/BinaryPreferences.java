package com.ironz.binaryprefs;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.name.KeyNameProvider;
import com.ironz.binaryprefs.util.Bits;
import com.ironz.binaryprefs.util.Constants;

import java.io.Externalizable;
import java.util.*;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final KeyNameProvider keyNameProvider;
    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter, ExceptionHandler exceptionHandler, KeyNameProvider keyNameProvider) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.keyNameProvider = keyNameProvider;
    }

    @Override
    public Map<String, ?> getAll() {
        try {
            return getAllInternal();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return new HashMap<>();
    }

    @Override
    public String getString(String key, String defValue) {
        if (!contains(key)) {
            return defValue;
        }
        try {
            return getStringInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (!contains(key)) {
            return defValues;
        }
        try {
            return getStringSetInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (!contains(key)) {
            return defValue;
        }
        try {
            return getIntInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (!contains(key)) {
            return defValue;
        }
        try {
            return getLongInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (!contains(key)) {
            return defValue;
        }
        try {
            return getFloatInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (!contains(key)) {
            return defValue;
        }
        try {
            return getBooleanInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public <T extends Externalizable> T getObject(Class<T> clazz, String key, T defValue) {
        if (!contains(key)) {
            return defValue;
        }
        try {
            return getObjectInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        return containsInternal(key);
    }

    @Override
    public PreferencesEditor edit() {
        return new BinaryPreferencesEditor(fileAdapter, exceptionHandler, listeners, this, keyNameProvider);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    private Map<String, ?> getAllInternal() {

        final Map<String, Object> map = new HashMap<>();

        for (String fileName : fileAdapter.names()) {

            String fileExtension = keyNameProvider.getFileExtension(fileName);
            String prefName = keyNameProvider.getKeyFromFileName(fileName);

            if (fileExtension.equals(Constants.STRING_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getStringInternal(prefName));
                continue;
            }
            if (fileExtension.equals(Constants.INTEGER_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getIntInternal(prefName));
                continue;
            }
            if (fileExtension.equals(Constants.LONG_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getLongInternal(prefName));
                continue;
            }
            if (fileExtension.equals(Constants.BOOLEAN_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getBooleanInternal(prefName));
                continue;
            }
            if (fileExtension.equals(Constants.FLOAT_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getFloatInternal(prefName));
                continue;
            }
            if (fileExtension.equals(Constants.STRING_SET_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getStringSetInternal(prefName));
            }
        }
        return map;
    }

    private String getStringInternal(String key) {
        String fileName = key + Constants.STRING_FILE_POSTFIX;
        byte[] bytes = fileAdapter.fetch(fileName);
        return new String(bytes);
    }

    private Set<String> getStringSetInternal(String key) {
        final HashSet<String> strings = new HashSet<>(0);
        final Set<String> files = new HashSet<>(Arrays.asList(fileAdapter.names()));
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String name = keyNameProvider.convertStringSetName(key, i);
            if (files.contains(name)) {
                byte[] bytes = fileAdapter.fetch(name);
                strings.add(new String(bytes));
                continue;
            }
            break;
        }
        return strings;
    }

    private int getIntInternal(String key) {
        String fileName = keyNameProvider.convertIntName(key);
        byte[] bytes = fileAdapter.fetch(fileName);
        return Bits.intFromBytes(bytes);
    }

    private long getLongInternal(String key) {
        String fileName = keyNameProvider.convertLongName(key);
        byte[] bytes = fileAdapter.fetch(fileName);
        return Bits.longFromBytes(bytes);
    }

    private float getFloatInternal(String key) {
        String fileName = keyNameProvider.convertFloatName(key);
        byte[] bytes = fileAdapter.fetch(fileName);
        return Bits.floatFromBytes(bytes);
    }

    private boolean getBooleanInternal(String key) {
        String fileName = keyNameProvider.convertBooleanName(key);
        byte[] bytes = fileAdapter.fetch(fileName);
        return Bits.booleanFromBytes(bytes);
    }

    private <T extends Externalizable> T getObjectInternal(String key) {
        return null;
    }

    private boolean containsInternal(String key) {
        for (String fileName : fileAdapter.names()) {
            if (keyNameProvider.getKeyFromFileName(fileName).equals(key)) {
                return true;
            }
        }
        return false;
    }
}