package com.ironz.binaryprefs.serialization;

import java.util.Set;

public class SerializerFactory {

    public SerializerFactory() {
        final Serializer<Boolean> booleanSerializer = new BooleanSerializerImpl();
        final Serializer<Byte> byteSerializer = new ByteSerializerImpl();
        final Serializer<Character> charSerializer = new CharSerializerImpl();
        final Serializer<Double> doubleSerializer = new DoubleSerializerImpl();
        final Serializer<Float> floatSerializer = new FloatSerializerImpl();
        final Serializer<Integer> integerSerializer = new IntegerSerializerImpl();
        final Serializer<Long> longSerializer = new LongSerializerImpl();
        final Serializer<Short> shortSerializer = new ShortSerializerImpl();
        final Serializer<String> stringSerializer = new StringSerializerImpl();
        final Serializer<Set<String>> stringSetSerializer = new StringSetSerializerImpl();

    }
}
