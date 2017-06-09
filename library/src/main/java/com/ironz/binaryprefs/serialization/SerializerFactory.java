package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;

import java.util.Set;

public final class SerializerFactory {

    /**
     * Empty token for determine empty object deserialization token
     */
    static final String EMPTY_TOKEN = "";

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

    public Object deserialize(String token, byte[] bytes) {
        byte flag = bytes[0];
        if (booleanSerializer.isMatches(flag)) {
            return booleanSerializer.deserialize(bytes);
        }
        if (byteSerializer.isMatches(flag)) {
            return byteSerializer.deserialize(bytes);
        }
        if (charSerializer.isMatches(flag)) {
            return charSerializer.deserialize(bytes);
        }
        if (doubleSerializer.isMatches(flag)) {
            return doubleSerializer.deserialize(bytes);
        }
        if (floatSerializer.isMatches(flag)) {
            return floatSerializer.deserialize(bytes);
        }
        if (integerSerializer.isMatches(flag)) {
            return integerSerializer.deserialize(bytes);
        }
        if (longSerializer.isMatches(flag)) {
            return longSerializer.deserialize(bytes);
        }
        if (shortSerializer.isMatches(flag)) {
            return shortSerializer.deserialize(bytes);
        }
        if (stringSerializer.isMatches(flag)) {
            return stringSerializer.deserialize(bytes);
        }
        if (stringSetSerializer.isMatches(flag)) {
            return stringSetSerializer.deserialize(bytes);
        }
        if (persistableSerializer.isMatches(flag)) {
            return persistableSerializer.deserialize(token, bytes);
        }
        throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect flag '%s'", flag));
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