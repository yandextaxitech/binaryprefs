package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Boolean to byte array implementation of {@link Serializer} and backwards
 */
public final class BooleanSerializerImpl implements Serializer<Boolean> {

    /**
     * Uses for detecting byte array primitive type of {@link Boolean}
     */
    private static final byte BOOLEAN_FLAG = -7;

    /**
     * Minimum size primitive type of {@link Boolean}
     */
    private static final int BOOLEAN_SIZE = 2;

    /**
     * Serialize {@code boolean} into byte array with following scheme:
     * [{@link #BOOLEAN_FLAG}] + [boolean_bytes].
     *
     * @param value target boolean to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Boolean value) {
        return new byte[]{
                BOOLEAN_FLAG,
                (byte) (value ? 1 : 0)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Boolean)} convention
     *
     * @param key   object token key
     * @param bytes target byte array for deserialization
     * @return deserialized boolean
     */
    @Override
    public Boolean deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, BOOLEAN_SIZE);
    }

    /**
     * Deserialize byte by {@link #serialize(Boolean)} convention
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized boolean
     */
    @Override
    public Boolean deserialize(String key, byte[] bytes, int offset, int length) {
        return bytes[1 + offset] != 0;
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == BOOLEAN_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Boolean;
    }

    @Override
    public int bytesLength() {
        return BOOLEAN_SIZE;
    }

    @Override
    public byte getFlag() {
        return BOOLEAN_FLAG;
    }
}