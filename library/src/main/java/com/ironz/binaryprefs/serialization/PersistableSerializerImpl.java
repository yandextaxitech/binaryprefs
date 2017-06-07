package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.persistable.Persistable;
import com.ironz.binaryprefs.serialization.persistable.PersistableClassProvider;
import com.ironz.binaryprefs.serialization.persistable.io.BinaryPrefsObjectInputImpl;
import com.ironz.binaryprefs.serialization.persistable.io.DataInput;

import java.util.Set;

/**
 * {@code Persistable} to byte array implementation of {@link Serializer} and backwards
 */
public final class PersistableSerializerImpl implements Serializer<Persistable> {

    /**
     * Minimum size primitive type of {@link Persistable}
     */
    private static final int SIZE_PERSISTABLE = 1;

    private final Serializer<Boolean> booleanSerializer;
    private final Serializer<Byte> byteSerializer;
    private final Serializer<Character> charSerializer;
    private final Serializer<Double> doubleSerializer;
    private final Serializer<Float> floatSerializer;
    private final Serializer<Integer> integerSerializer;
    private final Serializer<Long> longSerializer;
    private final Serializer<Short> shortSerializer;
    private final Serializer<String> stringSerializer;
    private final Serializer<Set<String>> stringSetSerializer; //temporary not used
    private final PersistableClassProvider persistableClassProvider;

    public PersistableSerializerImpl(Serializer<Boolean> booleanSerializer,
                                     Serializer<Byte> byteSerializer,
                                     Serializer<Character> charSerializer,
                                     Serializer<Double> doubleSerializer,
                                     Serializer<Float> floatSerializer,
                                     Serializer<Integer> integerSerializer,
                                     Serializer<Long> longSerializer,
                                     Serializer<Short> shortSerializer,
                                     Serializer<String> stringSerializer,
                                     Serializer<Set<String>> stringSetSerializer,
                                     PersistableClassProvider persistableClassProvider) {
        this.booleanSerializer = booleanSerializer;
        this.byteSerializer = byteSerializer;
        this.charSerializer = charSerializer;
        this.doubleSerializer = doubleSerializer;
        this.floatSerializer = floatSerializer;
        this.integerSerializer = integerSerializer;
        this.longSerializer = longSerializer;
        this.shortSerializer = shortSerializer;
        this.stringSerializer = stringSerializer;
        this.stringSetSerializer = stringSetSerializer;
        this.persistableClassProvider = persistableClassProvider;
    }

    @Override
    public byte[] serialize(Persistable persistable) {
        return new byte[0];
    }


    @Override
    public Persistable deserialize(byte[] bytes) {
        return null;
    }

    /**
     * Deserialize {@link Persistable} by {@link #serialize(Persistable)} convention
     *
     * @param key    key for deserialization token
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized {@link Persistable}
     */
    @Override
    public Persistable deserialize(String key, byte[] bytes, int offset, int length) {
        Class<? extends Persistable> clazz = persistableClassProvider.get(key);
        DataInput input = new BinaryPrefsObjectInputImpl(booleanSerializer,
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
        return SIZE_PERSISTABLE;
    }

    @Override
    public byte getFlag() {
        return Persistable.FLAG_PERSISTABLE;
    }
}