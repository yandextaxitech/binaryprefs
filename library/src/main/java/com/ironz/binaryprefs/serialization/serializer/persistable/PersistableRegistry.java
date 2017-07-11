package com.ironz.binaryprefs.serialization.serializer.persistable;

import java.util.HashMap;
import java.util.Map;

public final class PersistableRegistry {

    private final Map<String, Class<? extends Persistable>> map = new HashMap<>();

    public void register(String key, Class<? extends Persistable> clazz) {
        if (map.containsKey(key)) {
            throw new UnsupportedOperationException(String.format("Registry already contains '%s' class for '%s' key.", clazz.getName(), key));
        }
        map.put(key, clazz);
    }

    public Class<? extends Persistable> get(String key) {
        if (!map.containsKey(key)) {
            throw new UnsupportedClassVersionError(String.format("Cannot find class type for '%s' key. Please, add key and type in 'register' method.", key));
        }
        return map.get(key);
    }
}