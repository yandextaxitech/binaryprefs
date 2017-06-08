package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;

import java.util.Set;

public final class SerializerFactory {

    private final Serializer<Boolean> booleanSerializer;
    private final Serializer<Byte> byteSerializer;
    private final Serializer<Character> charSerializer;
    private final Serializer<Double> doubleSerializer;
    private final Serializer<Float> floatSerializer;
    private final Serializer<Integer> integerSerializer;
    private final Serializer<Long> longSerializer;
    private final Serializer<Short> shortSerializer;
    private final Serializer<String> stringSerializer;
    private final Serializer<Set<String>> stringSetSerializer;
    private final Serializer<Persistable> persistableSerializer;

    public SerializerFactory(PersistableRegistry classProvider) {
        booleanSerializer = new BooleanSerializerImpl();
        byteSerializer = new ByteSerializerImpl();
        charSerializer = new CharSerializerImpl();
        doubleSerializer = new DoubleSerializerImpl();
        floatSerializer = new FloatSerializerImpl();
        integerSerializer = new IntegerSerializerImpl();
        longSerializer = new LongSerializerImpl();
        shortSerializer = new ShortSerializerImpl();
        stringSerializer = new StringSerializerImpl();
        stringSetSerializer = new StringSetSerializerImpl();
        persistableSerializer = new PersistableSerializerImpl(
                booleanSerializer,
                byteSerializer,
                charSerializer,
                doubleSerializer,
                floatSerializer,
                integerSerializer,
                longSerializer,
                shortSerializer,
                stringSerializer,
                classProvider
        );
    }

    public Serializer getByClassType(Object o) {
        if (booleanSerializer.isMatches(o)) {
            return booleanSerializer;
        }
        if (byteSerializer.isMatches(o)) {
            return byteSerializer;
        }
        if (charSerializer.isMatches(o)) {
            return charSerializer;
        }
        if (doubleSerializer.isMatches(o)) {
            return doubleSerializer;
        }
        if (floatSerializer.isMatches(o)) {
            return floatSerializer;
        }
        if (integerSerializer.isMatches(o)) {
            return integerSerializer;
        }
        if (longSerializer.isMatches(o)) {
            return longSerializer;
        }
        if (shortSerializer.isMatches(o)) {
            return shortSerializer;
        }
        if (stringSerializer.isMatches(o)) {
            return stringSerializer;
        }
        if (stringSetSerializer.isMatches(o)) {
            return stringSetSerializer;
        }
        if (persistableSerializer.isMatches(o)) {
            return persistableSerializer;
        }
        throw new UnsupportedClassVersionError(String.format("Type verification failed. Incorrect type '%s'", o.getClass().getName()));
    }

    public Serializer getByFlag(byte flag) {
        if (booleanSerializer.isMatches(flag)) {
            return booleanSerializer;
        }
        if (byteSerializer.isMatches(flag)) {
            return byteSerializer;
        }
        if (charSerializer.isMatches(flag)) {
            return charSerializer;
        }
        if (doubleSerializer.isMatches(flag)) {
            return doubleSerializer;
        }
        if (floatSerializer.isMatches(flag)) {
            return floatSerializer;
        }
        if (integerSerializer.isMatches(flag)) {
            return integerSerializer;
        }
        if (longSerializer.isMatches(flag)) {
            return longSerializer;
        }
        if (shortSerializer.isMatches(flag)) {
            return shortSerializer;
        }
        if (stringSerializer.isMatches(flag)) {
            return stringSerializer;
        }
        if (stringSetSerializer.isMatches(flag)) {
            return stringSetSerializer;
        }
        if (persistableSerializer.isMatches(flag)) {
            return persistableSerializer;
        }
        throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect flag '%s'", flag));
    }

    public Serializer<Boolean> getBooleanSerializer() {
        return booleanSerializer;
    }

    public Serializer<Byte> getByteSerializer() {
        return byteSerializer;
    }

    public Serializer<Character> getCharSerializer() {
        return charSerializer;
    }

    public Serializer<Double> getDoubleSerializer() {
        return doubleSerializer;
    }

    public Serializer<Float> getFloatSerializer() {
        return floatSerializer;
    }

    public Serializer<Integer> getIntegerSerializer() {
        return integerSerializer;
    }

    public Serializer<Long> getLongSerializer() {
        return longSerializer;
    }

    public Serializer<Short> getShortSerializer() {
        return shortSerializer;
    }

    public Serializer<String> getStringSerializer() {
        return stringSerializer;
    }

    public Serializer<Set<String>> getStringSetSerializer() {
        return stringSetSerializer;
    }

    public Serializer<Persistable> getPersistableSerializer() {
        return persistableSerializer;
    }
}