package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Integer to byte array implementation of {@link Serializer} and backwards
 */
public final class IntegerSerializerImpl implements Serializer<Integer> {

    /**
     * Minimum size primitive type of {@link Integer}
     */
    private static final int INT_SIZE = 5;

    /**
     * Uses for detecting byte array primitive type of {@link Integer}
     */
    private static final byte INT_FLAG = (byte) -3;

    /**
     * Serialize {@code int} into byte array with following scheme:
     * [{@link #INT_FLAG}] + [int_bytes].
     *
     * @param value target int to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Integer value) {
        int i = 0xff;
        return new byte[]{
                INT_FLAG,
                (byte) ((value >>> 24) & i),
                (byte) ((value >>> 16) & i),
                (byte) ((value >>> 8) & i),
                (byte) ((value) & i)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Integer)} convention
     *
     * @param key   object token key
     * @param bytes target byte array for deserialization
     * @return deserialized int
     */
    @Override
    public Integer deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, INT_SIZE);
    }

    /**
     * Deserialize byte by {@link #serialize(Integer)} convention
     *
     * @param key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized int
     */
    @Override
    public Integer deserialize(String key, byte[] bytes, int offset, int length) {
        int i = 0xff;
        return ((bytes[4 + offset] & i)) +
                ((bytes[3 + offset] & i) << 8) +
                ((bytes[2 + offset] & i) << 16) +
                ((bytes[1 + offset]) << 24);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == INT_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Integer;
    }

    @Override
    public int bytesLength() {
        return INT_SIZE;
    }

    @Override
    public byte getFlag() {
        return INT_FLAG;
    }
}