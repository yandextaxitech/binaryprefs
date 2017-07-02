package com.ironz.binaryprefs.serialization.strategy.impl;

import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.CollectionStringSerializer;
import com.ironz.binaryprefs.serialization.serializer.StringSetSerializer;
import com.ironz.binaryprefs.serialization.strategy.SerializationStrategy;

import java.util.Collection;
import java.util.Set;

public final class CollectionStringSerializationStrategyImpl implements SerializationStrategy {

    private final Collection<String> value;
    private final CollectionStringSerializer collectionStringSerializer;

    public CollectionStringSerializationStrategyImpl(Collection<String> value, SerializerFactory serializerFactory) {
        this.value = value;
        this.collectionStringSerializer = serializerFactory.getCollectionStringSerializer();
    }

    @Override
    public byte[] serialize() {
        return collectionStringSerializer.serialize(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}