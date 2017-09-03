package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.PersistableSerializer;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class PersistableSerializationStrategy implements SerializationStrategy {

    private final Persistable value;
    private final PersistableSerializer persistableSerializer;

    public PersistableSerializationStrategy(Persistable value, SerializerFactory serializerFactory) {
        this.value = value;
        this.persistableSerializer = serializerFactory.getPersistableSerializer();
    }

    @Override
    public byte[] serialize() {
        return persistableSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value.deepClone();
    }
}