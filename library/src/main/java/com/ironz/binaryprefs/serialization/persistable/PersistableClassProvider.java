package com.ironz.binaryprefs.serialization.persistable;

import java.util.HashMap;
import java.util.Map;

public final class PersistableClassProvider {

    private final Map<String, Class<? extends Persistable>> map = new HashMap<>();

    public void define(String key, Class<? extends Persistable> clazz) {
        map.put(key, clazz);
    }

    public Class<? extends Persistable> get(String key) {
        if (!map.containsKey(key)) {
            throw new UnsupportedClassVersionError(String.format("Cannot find class type by '%s' key. Please, define it with 'define' method.", key));
        }
        return map.get(key);
    }
}