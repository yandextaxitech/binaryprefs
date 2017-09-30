package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.ShortSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class ShortSerializationStrategy implements SerializationStrategy {

    private final short value;
    private final ShortSerializer shortSerializer;

    public ShortSerializationStrategy(short value, SerializerFactory serializerFactory) {
        this.value = value;
        this.shortSerializer = serializerFactory.getShortSerializer();
    }

    @Override
    public byte[] serialize() {
        return shortSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}