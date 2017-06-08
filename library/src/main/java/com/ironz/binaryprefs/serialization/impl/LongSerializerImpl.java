package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Long to byte array implementation of {@link Serializer} and backwards
 */
public final class LongSerializerImpl implements Serializer<Long> {

    /**
     * Uses for detecting byte array primitive type of {@link Long}
     */
    private static final byte LONG_FLAG = -4;

    /**
     * Minimum size primitive type of {@link Long}
     */
    private static final int LONG_SIZE = 9;

    /**
     * Serialize {@code long} into byte array with following scheme:
     * [{@link #LONG_FLAG}] + [long_bytes].
     *
     * @param value target long to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Long value) {
        return new byte[]{
                LONG_FLAG,
                (byte) (value >>> 56),
                (byte) (value >>> 48),
                (byte) (value >>> 40),
                (byte) (value >>> 32),
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) (((long) value))
        };
    }


    /**
     * Deserialize byte by {@link #serialize(Long)} convention
     *
     *
     * @param key    object token key
     * @param bytes target byte array for deserialization
     * @return deserialized long
     */
    @Override
    public Long deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, bytes.length);
    }

    /**
     * Deserialize byte by {@link #serialize(Long)} convention
     *
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized long
     */
    @Override
    public Long deserialize(String key, byte[] bytes, int offset, int length) {
        long l = 0xffL;
        return ((bytes[8 + offset] & l)) +
                ((bytes[7 + offset] & l) << 8) +
                ((bytes[6 + offset] & l) << 16) +
                ((bytes[5 + offset] & l) << 24) +
                ((bytes[4 + offset] & l) << 32) +
                ((bytes[3 + offset] & l) << 40) +
                ((bytes[2 + offset] & l) << 48) +
                (((long) bytes[1 + offset]) << 56);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == LONG_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Long;
    }

    @Override
    public int bytesLength() {
        return LONG_SIZE;
    }

    @Override
    public byte getFlag() {
        return LONG_FLAG;
    }
}