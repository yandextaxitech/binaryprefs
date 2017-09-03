package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.LongSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class LongSerializationStrategy implements SerializationStrategy {

    private final long value;
    private final LongSerializer longSerializer;

    public LongSerializationStrategy(long value, SerializerFactory serializerFactory) {
        this.value = value;
        this.longSerializer = serializerFactory.getLongSerializer();
    }

    @Override
    public byte[] serialize() {
        return longSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}