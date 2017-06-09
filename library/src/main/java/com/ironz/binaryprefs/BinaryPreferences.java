package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.task.TaskExecutor;

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

    @SuppressWarnings("WeakerAccess")
    public BinaryPreferences(FileAdapter fileAdapter,
                             ExceptionHandler exceptionHandler,
                             EventBridge eventsBridge,
                             CacheProvider cacheProvider,
                             TaskExecutor taskExecutor,
                             SerializerFactory serializerFactory) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
        this.eventsBridge = eventsBridge;
        this.cacheProvider = cacheProvider;
        this.taskExecutor = taskExecutor;
        this.serializerFactory = serializerFactory;
        defineCache();
    }

    private void defineCache() {
        synchronized (Preferences.class) {
            for (String name : fileAdapter.names()) {
                try {
                    byte[] bytes = fileAdapter.fetch(name);
                    cacheProvider.put(name, bytes);
                } catch (Exception e) {
                    exceptionHandler.handle(e, name);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getAll() {
        synchronized (Preferences.class) {
            try {
                return getAllInternal();
            } catch (Exception e) {
                exceptionHandler.handle(e, "getAll method");
            }
            return new HashMap<>();
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (Preferences.class) {
            try {
                return getStringInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        synchronized (Preferences.class) {
            try {
                return getStringSetInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValues;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (Preferences.class) {
            try {
                return getIntInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (Preferences.class) {
            try {
                return getLongInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (Preferences.class) {
            try {
                return getFloatInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (Preferences.class) {
            try {
                return getBooleanInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public <T extends Persistable> T getPersistable(String key, T defValue) {
        synchronized (Preferences.class) {
            try {
                return getPersistableInternal(key);
            } catch (Exception e) {
                exceptionHandler.handle(e, key);
            }
            return defValue;
        }
    }

    @Override
    public boolean contains(String key) {
        synchronized (Preferences.class) {
            return containsInternal(key);
        }
    }

    @Override
    public PreferencesEditor edit() {
        synchronized (Preferences.class) {
            return new BinaryPreferencesEditor(
                    this,
                    fileAdapter,
                    exceptionHandler,
                    eventsBridge,
                    taskExecutor,
                    serializerFactory,
                    cacheProvider
            );
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (Preferences.class) {
            eventsBridge.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (Preferences.class) {
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

    private boolean containsInternal(String key) {
        return cacheProvider.contains(key);
    }
}