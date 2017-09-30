package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.IntegerSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class IntegerSerializationStrategy implements SerializationStrategy {

    private final int value;
    private final IntegerSerializer integerSerializer;

    public IntegerSerializationStrategy(int value, SerializerFactory serializerFactory) {
        this.value = value;
        this.integerSerializer = serializerFactory.getIntegerSerializer();
    }

    @Override
    public byte[] serialize() {
        return integerSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}