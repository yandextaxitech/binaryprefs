package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.StringSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

public final class StringSerializationStrategy implements SerializationStrategy {

    private final String value;
    private final StringSerializer stringSerializer;

    public StringSerializationStrategy(String value, SerializerFactory serializerFactory) {
        this.value = value;
        this.stringSerializer = serializerFactory.getStringSerializer();
    }

    @Override
    public byte[] serialize() {
        return stringSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}