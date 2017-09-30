package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.BooleanSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class BooleanSerializationStrategy implements SerializationStrategy {

    private final boolean value;
    private final BooleanSerializer booleanSerializer;

    public BooleanSerializationStrategy(boolean value, SerializerFactory serializerFactory) {
        this.value = value;
        this.booleanSerializer = serializerFactory.getBooleanSerializer();
    }

    @Override
    public byte[] serialize() {
        return booleanSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}