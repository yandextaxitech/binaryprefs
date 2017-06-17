package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.StringSetSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

import java.util.Set;

public final class StringSetSerializationStrategyImpl implements SerializationStrategy {

    private final Set<String> value;
    private final StringSetSerializer stringSetSerializer;

    public StringSetSerializationStrategyImpl(Set<String> value, SerializerFactory serializerFactory) {
        this.value = value;
        this.stringSetSerializer = serializerFactory.getStringSetSerializer();
    }

    @Override
    public byte[] serialize() {
        return stringSetSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}