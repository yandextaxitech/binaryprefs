package com.ironz.binaryprefs.serialization.serializer.persistable;

import java.util.HashMap;
import java.util.Map;

public final class PersistableRegistry {

    private final Map<String, Class<? extends Persistable>> map = new HashMap<>();

    public void register(String token, Class<? extends Persistable> clazz) {
        if (map.containsKey(token)) {
            throw new UnsupportedOperationException(String.format("Registry already contains '%s' class for '%s' token.", clazz.getName(), token));
        }
        map.put(token, clazz);
    }

    public Class<? extends Persistable> get(String token) {
        if (!map.containsKey(token)) {
            throw new UnsupportedClassVersionError(String.format("Cannot find class type for '%s' token. Please, add token and type in 'register' method.", token));
        }
        return map.get(token);
    }

    public void remove(String token) {
        map.remove(token);
    }
}