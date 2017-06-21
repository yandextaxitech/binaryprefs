package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.serializer.*;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;

import java.util.Collections;
import java.util.Set;

/**
 * Contains all serializers which possible for data transformation.
 * This is non-public api class.
 */
public final class SerializerFactory {

    private final BooleanSerializer booleanSerializer;
    private final ByteSerializer byteSerializer;
    private final CharSerializer charSerializer;
    private final DoubleSerializer doubleSerializer;
    private final FloatSerializer floatSerializer;
    private final IntegerSerializer integerSerializer;
    private final LongSerializer longSerializer;
    private final ShortSerializer shortSerializer;
    private final StringSerializer stringSerializer;
    private final StringSetSerializer stringSetSerializer;
    private final PersistableSerializer persistableSerializer;

    public SerializerFactory(PersistableRegistry persistableRegistry) {
        booleanSerializer = new BooleanSerializer();
        byteSerializer = new ByteSerializer();
        charSerializer = new CharSerializer();
        doubleSerializer = new DoubleSerializer();
        floatSerializer = new FloatSerializer();
        integerSerializer = new IntegerSerializer();
        longSerializer = new LongSerializer();
        shortSerializer = new ShortSerializer();
        stringSerializer = new StringSerializer();
        stringSetSerializer = new StringSetSerializer();
        persistableSerializer = new PersistableSerializer(
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

    public Object deserialize(String key, byte[] bytes) {
        byte flag = bytes[0];
        if (booleanSerializer.isMatches(flag)) {
            return booleanSerializer.deserialize(bytes);
        }
        if (integerSerializer.isMatches(flag)) {
            return integerSerializer.deserialize(bytes);
        }
        if (longSerializer.isMatches(flag)) {
            return longSerializer.deserialize(bytes);
        }
        if (doubleSerializer.isMatches(flag)) {
            return doubleSerializer.deserialize(bytes);
        }
        if (floatSerializer.isMatches(flag)) {
            return floatSerializer.deserialize(bytes);
        }
        if (stringSerializer.isMatches(flag)) {
            return stringSerializer.deserialize(bytes);
        }
        if (stringSetSerializer.isMatches(flag)) {
            return stringSetSerializer.deserialize(bytes);
        }
        if (persistableSerializer.isMatches(flag)) {
            return persistableSerializer.deserialize(key, bytes);
        }
        if (shortSerializer.isMatches(flag)) {
            return shortSerializer.deserialize(bytes);
        }
        if (byteSerializer.isMatches(flag)) {
            return byteSerializer.deserialize(bytes);
        }
        if (charSerializer.isMatches(flag)) {
            return charSerializer.deserialize(bytes);
        }
        throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect flag '%s'", flag));
    }

    public byte[] serialize(Object o) {
        if (o instanceof Boolean) {
            return booleanSerializer.serialize((boolean) o);
        }
        if (o instanceof Integer) {
            return integerSerializer.serialize((int) o);
        }
        if (o instanceof Long) {
            return longSerializer.serialize((long) o);
        }
        if (o instanceof Double) {
            return doubleSerializer.serialize((double) o);
        }
        if (o instanceof Float) {
            return floatSerializer.serialize((float) o);
        }
        if (o instanceof String) {
            return stringSerializer.serialize((String) o);
        }
        if (o instanceof Set) {
            //noinspection unchecked
            return stringSetSerializer.serialize((Set<String>) o);
        }
        if (o instanceof Persistable) {
            return persistableSerializer.serialize((Persistable) o);
        }
        if (o instanceof Short) {
            return shortSerializer.serialize((short) o);
        }
        if (o instanceof Byte) {
            return byteSerializer.serialize((byte) o);
        }
        if (o instanceof Character) {
            return charSerializer.serialize((char) o);
        }
        throw new UnsupportedClassVersionError(String.format("Unsupported serialization object format '%s'", o));
    }

    public Object redefineMutable(Object o) {
        if (o instanceof Set) {
            //noinspection unchecked
            return Collections.unmodifiableSet((Set<String>) o);
        }
        if (o instanceof Persistable) {
            return ((Persistable) o).deepCopy();
        }
        return o;
    }

    public BooleanSerializer getBooleanSerializer() {
        return booleanSerializer;
    }

    public ByteSerializer getByteSerializer() {
        return byteSerializer;
    }

    public CharSerializer getCharSerializer() {
        return charSerializer;
    }

    public DoubleSerializer getDoubleSerializer() {
        return doubleSerializer;
    }

    public FloatSerializer getFloatSerializer() {
        return floatSerializer;
    }

    public IntegerSerializer getIntegerSerializer() {
        return integerSerializer;
    }

    public LongSerializer getLongSerializer() {
        return longSerializer;
    }

    public ShortSerializer getShortSerializer() {
        return shortSerializer;
    }

    public StringSerializer getStringSerializer() {
        return stringSerializer;
    }

    public StringSetSerializer getStringSetSerializer() {
        return stringSetSerializer;
    }

    public PersistableSerializer getPersistableSerializer() {
        return persistableSerializer;
    }
}