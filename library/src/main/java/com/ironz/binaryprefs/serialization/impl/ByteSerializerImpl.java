package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Byte to byte array implementation of {@link Serializer} and backwards
 */
public final class ByteSerializerImpl implements Serializer<Byte> {

    /**
     * Uses for detecting byte primitive type of {@link Byte}
     */
    private static final byte BYTE_FLAG = -8;

    /**
     * Minimum size primitive type of {@link Byte}
     */
    private static final int BYTE_SIZE = 2;

    /**
     * Serialize {@code byte} into byte array with following scheme:
     * [{@link #BYTE_FLAG}] + [byte].
     *
     * @param value target byte to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Byte value) {
        return new byte[]{
                BYTE_FLAG,
                value
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Byte)}  convention
     *
     * @param key    object token key
     * @param bytes target byte array for deserialization
     * @return deserialized byte
     */
    @Override
    public Byte deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, BYTE_SIZE);
    }

    /**
     * Deserialize byte by {@link #serialize(Byte)}  convention
     *
     *
     * @param key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized byte
     */
    @Override
    public Byte deserialize(String key, byte[] bytes, int offset, int length) {
        return bytes[1 + offset];
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == BYTE_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Byte;
    }

    @Override
    public int bytesLength() {
        return BYTE_SIZE;
    }

    @Override
    public byte getFlag() {
        return BYTE_FLAG;
    }
}