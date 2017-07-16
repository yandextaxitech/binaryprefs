package com.ironz.binaryprefs.serialization.serializer.persistable;

import java.util.HashMap;
import java.util.Map;

public final class PersistableRegistry {

    private static final String CANNOT_FIND_MESSAGE = "Cannot find Persistable type for '%s' key. " +
            "Please, add it through 'registerPersistable' builder method.";
    private static final String ALREADY_REGISTERED_MESSAGE = "Registry already contains '%s' class for '%s' key. " +
            "Please, don't add persistable by similar key twice.";

    private final Map<String, Class<? extends Persistable>> map = new HashMap<>();

    public void register(String key, Class<? extends Persistable> clazz) {
        if (map.containsKey(key)) {
            throw new UnsupportedOperationException(String.format(ALREADY_REGISTERED_MESSAGE, clazz.getName(), key));
        }
        map.put(key, clazz);
    }

    public Class<? extends Persistable> get(String key) {
        if (!map.containsKey(key)) {
            throw new UnsupportedClassVersionError(String.format(CANNOT_FIND_MESSAGE, key));
        }
        return map.get(key);
    }
}