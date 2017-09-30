package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.ByteSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class ByteSerializationStrategy implements SerializationStrategy {

    private final byte value;
    private final ByteSerializer byteSerializer;

    public ByteSerializationStrategy(byte value, SerializerFactory serializerFactory) {
        this.value = value;
        this.byteSerializer = serializerFactory.getByteSerializer();
    }

    @Override
    public byte[] serialize() {
        return byteSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}