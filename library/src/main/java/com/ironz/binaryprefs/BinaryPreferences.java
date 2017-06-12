package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements Preferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;
    private final EventBridge eventsBridge;
    private final CacheProvider cacheProvider;
    private final TaskExecutor taskExecutor;
    private final SerializerFactory serializerFactory;
    private final Object lock;

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter,
                             ExceptionHandler exceptionHandler,
                             EventBridge eventsBridge,
                             CacheProvider cacheProvider,
                             TaskExecutor taskExecutor,
                             SerializerFactory serializerFactory,
                             LockFactory lockFactory) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.eventsBridge = eventsBridge;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        this.lock = lockFactory.get();
        fetchCache();
    }

    private void fetchCache() {
        synchronized (lock) {
            for (String name : fileAdapter.names()) {
                try {
                    byte[] bytes = fileAdapter.fetch(name);
                    cacheProvider.put(name, bytes);
                } catch (Exception e) {
                    exceptionHandler.handle(name, e);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getAll() {
        synchronized (lock) {
            try {
                return getAllInternal();
            } catch (Exception e) {
                exceptionHandler.handle("getAll method", e);
            }
            return Collections.emptyMap();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (lock) {
            try {
                return getStringInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(key, e);
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
                exceptionHandler.handle(key, e);
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
                exceptionHandler.handle(key, e);
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
                exceptionHandler.handle(key, e);
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
                exceptionHandler.handle(key, e);
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
                exceptionHandler.handle(key, e);
            }
            return defValue;
        }
    }

    @Override
    public <T extends Persistable> T getPersistable(String key, T defValue) {
        synchronized (lock) {
            try {
                return getPersistableInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(key, e);
            }
            return defValue;
        }
    }

    @Override
    public byte getByte(String key, byte defValue) {
        synchronized (lock) {
            try {
                return getByteInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(key, e);
            }
            return defValue;
        }
    }

    @Override
    public short getShort(String key, short defValue) {
        synchronized (lock) {
            try {
                return getShortInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(key, e);
            }
            return defValue;
        }
    }

    @Override
    public char getChar(String key, char defValue) {
        synchronized (lock) {
            try {
                return getCharInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(key, e);
            }
            return defValue;
        }
    }

    @Override
    public double getDouble(String key, double defValue) {
        synchronized (lock) {
            try {
                return getDoubleInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(key, e);
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
            return new BinaryPreferencesEditor(
                    this,
                    fileAdapter,
                    exceptionHandler,
                    eventsBridge,
                    taskExecutor,
                    serializerFactory,
                    cacheProvider,
                    lock
            );
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (lock) {
            eventsBridge.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (lock) {
            eventsBridge.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    private Map<String, Object> getAllInternal() {
        Map<String, Object> map = new HashMap<>();
        for (String key : cacheProvider.keys()) {
            byte[] bytes = cacheProvider.get(key);
            Object o = serializerFactory.deserialize(key, bytes);
            map.put(key, o);
        }
        return map;
    }

    private String getStringInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        StringSerializer serializer = serializerFactory.getStringSerializer();
        return serializer.deserialize(bytes);
    }

    private Set<String> getStringSetInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        StringSetSerializer serializer = serializerFactory.getStringSetSerializer();
        return serializer.deserialize(bytes);
    }

    private int getIntInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        IntegerSerializer serializer = serializerFactory.getIntegerSerializer();
        return serializer.deserialize(bytes);
    }

    private long getLongInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        LongSerializer serializer = serializerFactory.getLongSerializer();
        return serializer.deserialize(bytes);
    }

    private float getFloatInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        FloatSerializer serializer = serializerFactory.getFloatSerializer();
        return serializer.deserialize(bytes);
    }

    private boolean getBooleanInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        BooleanSerializer serializer = serializerFactory.getBooleanSerializer();
        return serializer.deserialize(bytes);
    }

    private <T extends Persistable> T getPersistableInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        PersistableSerializer serializer = serializerFactory.getPersistableSerializer();
        //noinspection unchecked
        return (T) serializer.deserialize(key, bytes);
    }

    private byte getByteInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        ByteSerializer byteSerializer = serializerFactory.getByteSerializer();
        return byteSerializer.deserialize(bytes);
    }

    private short getShortInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        ShortSerializer byteSerializer = serializerFactory.getShortSerializer();
        return byteSerializer.deserialize(bytes);
    }

    private char getCharInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        CharSerializer byteSerializer = serializerFactory.getCharSerializer();
        return byteSerializer.deserialize(bytes);
    }

    private double getDoubleInternal(String key) {
        byte[] bytes = cacheProvider.get(key);
        DoubleSerializer byteSerializer = serializerFactory.getDoubleSerializer();
        return byteSerializer.deserialize(bytes);
    }

    private boolean containsInternal(String key) {
        return cacheProvider.contains(key);
    }
}