package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.DoubleSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class DoubleSerializationStrategyImpl implements SerializationStrategy {

    private final double value;
    private final DoubleSerializer doubleSerializer;

    public DoubleSerializationStrategyImpl(double value, SerializerFactory serializerFactory) {
        this.value = value;
        this.doubleSerializer = serializerFactory.getDoubleSerializer();
    }

    @Override
    public byte[] serialize() {
        return doubleSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}