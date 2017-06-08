package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;

import java.util.Set;

public final class SerializerFactory {

    private final BooleanSerializer booleanSerializer;
    private final ByteSerializerImpl byteSerializer;
    private final CharSerializerImpl charSerializer;
    private final DoubleSerializerImpl doubleSerializer;
    private final FloatSerializerImpl floatSerializer;
    private final IntegerSerializerImpl integerSerializer;
    private final LongSerializerImpl longSerializer;
    private final ShortSerializerImpl shortSerializer;
    private final StringSerializerImpl stringSerializer;
    private final StringSetSerializerImpl stringSetSerializer;
    private final PersistableSerializerImpl persistableSerializer;

    public SerializerFactory(PersistableRegistry persistableRegistry) {
        booleanSerializer = new BooleanSerializer();
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
                persistableRegistry
        );
    }

    public byte[] serialize(Object o) {
        if (booleanSerializer.isMatches(o)) {
            return booleanSerializer.serialize(((boolean) o));
        }
        if (byteSerializer.isMatches(o)) {
            return byteSerializer.serialize(((byte) o));
        }
        if (charSerializer.isMatches(o)) {
            return charSerializer.serialize((char) o);
        }
        if (doubleSerializer.isMatches(o)) {
            return doubleSerializer.serialize((double) o);
        }
        if (floatSerializer.isMatches(o)) {
            return floatSerializer.serialize((float) o);
        }
        if (integerSerializer.isMatches(o)) {
            return integerSerializer.serialize((int) o);
        }
        if (longSerializer.isMatches(o)) {
            return longSerializer.serialize((long) o);
        }
        if (shortSerializer.isMatches(o)) {
            return shortSerializer.serialize((short) o);
        }
        if (stringSerializer.isMatches(o)) {
            return stringSerializer.serialize((String) o);
        }
        if (stringSetSerializer.isMatches(o)) {
            //noinspection unchecked
            return stringSetSerializer.serialize((Set<String>) o);
        }
        if (persistableSerializer.isMatches(o)) {
            return persistableSerializer.serialize((Persistable) o);
        }
        throw new UnsupportedClassVersionError(String.format("Type verification failed. Incorrect type '%s'", o.getClass().getName()));
    }

    public Object deserialize(String key, byte[] bytes) {
        if (booleanSerializer.isMatches(bytes)) {
            return booleanSerializer.deserialize(bytes);
        }
        if (byteSerializer.isMatches(bytes)) {
            return byteSerializer.deserialize(bytes);
        }
        if (charSerializer.isMatches(bytes)) {
            return charSerializer.deserialize(bytes);
        }
        if (doubleSerializer.isMatches(bytes)) {
            return doubleSerializer.deserialize(bytes);
        }
        if (floatSerializer.isMatches(bytes)) {
            return floatSerializer.deserialize(bytes);
        }
        if (integerSerializer.isMatches(bytes)) {
            return integerSerializer.deserialize(bytes);
        }
        if (longSerializer.isMatches(bytes)) {
            return longSerializer.deserialize(bytes);
        }
        if (shortSerializer.isMatches(bytes)) {
            return shortSerializer.deserialize(bytes);
        }
        if (stringSerializer.isMatches(bytes)) {
            return stringSerializer.deserialize(bytes);
        }
        if (stringSetSerializer.isMatches(bytes)) {
            return stringSetSerializer.deserialize(bytes);
        }
        if (persistableSerializer.isMatches(bytes)) {
            return persistableSerializer.deserialize(key, bytes);
        }
        throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect flag '%s'", bytes));
    }

    public BooleanSerializer getBooleanSerializer() {
        return booleanSerializer;
    }

    public ByteSerializerImpl getByteSerializer() {
        return byteSerializer;
    }

    public CharSerializerImpl getCharSerializer() {
        return charSerializer;
    }

    public DoubleSerializerImpl getDoubleSerializer() {
        return doubleSerializer;
    }

    public FloatSerializerImpl getFloatSerializer() {
        return floatSerializer;
    }

    public IntegerSerializerImpl getIntegerSerializer() {
        return integerSerializer;
    }

    public LongSerializerImpl getLongSerializer() {
        return longSerializer;
    }

    public ShortSerializerImpl getShortSerializer() {
        return shortSerializer;
    }

    public StringSerializerImpl getStringSerializer() {
        return stringSerializer;
    }

    public StringSetSerializerImpl getStringSetSerializer() {
        return stringSetSerializer;
    }

    public PersistableSerializerImpl getPersistableSerializer() {
        return persistableSerializer;
    }
}