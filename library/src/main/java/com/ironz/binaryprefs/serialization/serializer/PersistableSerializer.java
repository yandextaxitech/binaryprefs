package com.ironz.binaryprefs.serialization.serializer;

import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.serialization.serializer.persistable.io.BinaryPrefsObjectInputImpl;
import com.ironz.binaryprefs.serialization.serializer.persistable.io.BinaryPrefsObjectOutputImpl;
import com.ironz.binaryprefs.serialization.serializer.persistable.io.DataInput;
import com.ironz.binaryprefs.serialization.serializer.persistable.io.DataOutput;

/**
 * {@code Persistable} to byte array implementation and backwards
 */
public final class PersistableSerializer {

    /**
     * Uses for detecting byte array primitive type of {@link Persistable}
     */
    public static final byte FLAG = -11;

    private final BooleanSerializer booleanSerializer;
    private final ByteSerializer byteSerializer;
    private final CharSerializer charSerializer;
    private final DoubleSerializer doubleSerializer;
    private final FloatSerializer floatSerializer;
    private final IntegerSerializer integerSerializer;
    private final LongSerializer longSerializer;
    private final ShortSerializer shortSerializer;
    private final StringSerializer stringSerializer;
    private final PersistableRegistry persistableRegistry;

    public PersistableSerializer(BooleanSerializer booleanSerializer,
                                 ByteSerializer byteSerializer,
                                 CharSerializer charSerializer,
                                 DoubleSerializer doubleSerializer,
                                 FloatSerializer floatSerializer,
                                 IntegerSerializer integerSerializer,
                                 LongSerializer longSerializer,
                                 ShortSerializer shortSerializer,
                                 StringSerializer stringSerializer,
                                 PersistableRegistry persistableRegistry) {
        this.booleanSerializer = booleanSerializer;
        this.byteSerializer = byteSerializer;
        this.charSerializer = charSerializer;
        this.doubleSerializer = doubleSerializer;
        this.floatSerializer = floatSerializer;
        this.integerSerializer = integerSerializer;
        this.longSerializer = longSerializer;
        this.shortSerializer = shortSerializer;
        this.stringSerializer = stringSerializer;
        this.persistableRegistry = persistableRegistry;
    }

    /**
     * Serialize {@code Persistable} into byte array with following scheme:
     * [{@link PersistableSerializer#FLAG}] + [boolean_bytes].
     *
     * @param value target persistable to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(Persistable value) {
        DataOutput output = new BinaryPrefsObjectOutputImpl(
                booleanSerializer,
                byteSerializer,
                charSerializer,
                doubleSerializer,
                floatSerializer,
                integerSerializer,
                longSerializer,
                shortSerializer,
                stringSerializer
        );
        return output.serialize(value);
    }

    /**
     * Deserialize {@link Persistable} by {@link #serialize(Persistable)} convention
     *
     * @param token token for determinate how to serialize
     *              one type of class type or interface type by two or more
     *              different serialization protocols.
     * @param bytes target byte array for deserialization
     * @return deserialized {@link Persistable}
     */
    public Persistable deserialize(String token, byte[] bytes) {
        Class<? extends Persistable> clazz = persistableRegistry.get(token);
        DataInput input = new BinaryPrefsObjectInputImpl(
                booleanSerializer,
                byteSerializer,
                charSerializer,
                doubleSerializer,
                floatSerializer,
                integerSerializer,
                longSerializer,
                shortSerializer,
                stringSerializer
        );
        return input.deserialize(bytes, clazz);
    }

    public boolean isMatches(byte flag) {
        return flag == FLAG;
    }
}