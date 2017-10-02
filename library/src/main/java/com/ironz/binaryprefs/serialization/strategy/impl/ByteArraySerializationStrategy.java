package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.ByteArraySerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class ByteArraySerializationStrategy implements SerializationStrategy {

    private final byte[] value;
    private final ByteArraySerializer byteArraySerializer;

    public ByteArraySerializationStrategy(byte[] value, SerializerFactory serializerFactory) {
        this.value = value;
        this.byteArraySerializer = serializerFactory.getByteArraySerializer();
    }

    @Override
    public byte[] serialize() {
        return byteArraySerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}