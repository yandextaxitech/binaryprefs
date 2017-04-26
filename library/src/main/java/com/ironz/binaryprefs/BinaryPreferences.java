package com.ironz.binaryprefs;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.util.Bits;

import java.io.Externalizable;
import java.util.*;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();
    private final Class lock = BinaryPreferences.class;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter, ExceptionHandler exceptionHandler) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
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
            return new BinaryPreferencesEditor(lock, fileAdapter, exceptionHandler, listeners, this);
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
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private String getStringInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key);
        return Bits.stringFromBytes(bytes);
    }

    private Set<String> getStringSetInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key);
        return Bits.stringSetFromBytes(bytes);
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
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private boolean containsInternal(String key) {
        return fileAdapter.contains(key);
    }
}