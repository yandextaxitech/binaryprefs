package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.FloatSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class FloatSerializationStrategy implements SerializationStrategy {

    private final float value;
    private final FloatSerializer floatSerializer;

    public FloatSerializationStrategy(float value, SerializerFactory serializerFactory) {
        this.value = value;
        this.floatSerializer = serializerFactory.getFloatSerializer();
    }

    @Override
    public byte[] serialize() {
        return floatSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}