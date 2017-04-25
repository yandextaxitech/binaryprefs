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
    private final Class lock = BinaryPreferences.class;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter, ExceptionHandler exceptionHandler, KeyNameProvider keyNameProvider) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.keyNameProvider = keyNameProvider;
    }

    @Override
    public Map<String, ?> getAll() {
        synchronized (lock) {
            try {
                return getAllInternal();
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
            return new HashMap<>();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (lock) {
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
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        synchronized (lock) {
            if (!contains(key)) {
                return defValues;
            }
            if (!fileAdapter.isDirectory(key)) {
                throw new ClassCastException(String.format("Value by %s key contains not Set<String>!", key));
            }
            try {
                return getStringSetInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
            return defValues;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (lock) {
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
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (lock) {
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
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (lock) {
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
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (lock) {
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
    }

    @Override
    public <T extends Externalizable> T getObject(Class<T> clazz, String key, T defValue) {
        synchronized (lock) {
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
    }

    @Override
    public boolean contains(String key) {
        synchronized (lock) {
            return containsInternal(key);
        }
    }

    @Override
    public PreferencesEditor edit() {
        synchronized (lock) {
            return new BinaryPreferencesEditor(lock, fileAdapter, exceptionHandler, listeners, this, keyNameProvider);
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (lock) {
            listeners.remove(listener);
        }
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
        byte[] bytes = fileAdapter.fetch(key);
        return new String(bytes);
    }

    private Set<String> getStringSetInternal(String key) {

        byte[][] all = fileAdapter.fetchAll(key);

        HashSet<String> strings = new HashSet<>(all.length);

        for (byte[] bytes : all) {
            String s = Bits.stringFromBytes(bytes);
            strings.add(s);
        }

        return strings;
    }

    private int getIntInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key);
        return Bits.intFromBytes(bytes);
    }

    private long getLongInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key);
        return Bits.longFromBytes(bytes);
    }

    private float getFloatInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key);
        return Bits.floatFromBytes(bytes);
    }

    private boolean getBooleanInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key);
        return Bits.booleanFromBytes(bytes);
    }

    private <T extends Externalizable> T getObjectInternal(String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean containsInternal(String key) {
        return fileAdapter.contains(key);
    }
}