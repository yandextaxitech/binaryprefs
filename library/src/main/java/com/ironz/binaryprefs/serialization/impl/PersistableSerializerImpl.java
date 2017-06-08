package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;
import com.ironz.binaryprefs.serialization.impl.persistable.io.BinaryPrefsObjectInputImpl;
import com.ironz.binaryprefs.serialization.impl.persistable.io.BinaryPrefsObjectOutputImpl;
import com.ironz.binaryprefs.serialization.impl.persistable.io.DataInput;
import com.ironz.binaryprefs.serialization.impl.persistable.io.DataOutput;

/**
 * {@code Persistable} to byte array implementation of {@link Serializer} and backwards
 */
public final class PersistableSerializerImpl implements Serializer<Persistable> {

    /**
     * Minimum size primitive type of {@link Persistable}
     */
    private static final int PERSISTABLE_SIZE = 1;

    private final Serializer<Boolean> booleanSerializer;
    private final Serializer<Byte> byteSerializer;
    private final Serializer<Character> charSerializer;
    private final Serializer<Double> doubleSerializer;
    private final Serializer<Float> floatSerializer;
    private final Serializer<Integer> integerSerializer;
    private final Serializer<Long> longSerializer;
    private final Serializer<Short> shortSerializer;
    private final Serializer<String> stringSerializer;
    private final PersistableRegistry persistableRegistry;

    public PersistableSerializerImpl(Serializer<Boolean> booleanSerializer,
                                     Serializer<Byte> byteSerializer,
                                     Serializer<Character> charSerializer,
                                     Serializer<Double> doubleSerializer,
                                     Serializer<Float> floatSerializer,
                                     Serializer<Integer> integerSerializer,
                                     Serializer<Long> longSerializer,
                                     Serializer<Short> shortSerializer,
                                     Serializer<String> stringSerializer,
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
     * [{@link Persistable#FLAG_PERSISTABLE}] + [boolean_bytes].
     *
     * @param value target persistable to serialize.
     * @return specific byte array with scheme.
     */
    @Override
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
     * @param key   token for determinate how to serialize
     *              one type of class type or interface type by two or more
     *              different serialization protocols.
     *              Default key is {@link #EMPTY_KEY}
     * @param bytes target byte array for deserialization
     * @return deserialized {@link Persistable}
     */
    @Override
    public Persistable deserialize(String key, byte[] bytes) {
        return deserialize(key, bytes, 0, bytes.length);
    }

    /**
     * Deserialize {@link Persistable} by {@link #serialize(Persistable)} convention
     *
     * @param key    token for determinate how to serialize
     *               one type of class type or interface type by two or more
     *               different serialization protocols.
     *               Default key is {@link #EMPTY_KEY}
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized {@link Persistable}
     */
    @Override
    public Persistable deserialize(String key, byte[] bytes, int offset, int length) {
        Class<? extends Persistable> clazz = persistableRegistry.get(key);
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

    @Override
    public boolean isMatches(byte flag) {
        return flag == Persistable.FLAG_PERSISTABLE;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Persistable;
    }

    @Override
    public int bytesLength() {
        return PERSISTABLE_SIZE;
    }
}