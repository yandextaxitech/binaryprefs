package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Float to byte array implementation of {@link Serializer} and backwards
 */
public final class FloatSerializerImpl implements Serializer<Float> {

    /**
     * Uses for detecting byte array primitive type of {@link Float}
     */
    public static final byte FLOAT_FLAG = -6;

    /**
     * Minimum size primitive type of {@link Float}
     */
    private static final int FLOAT_SIZE = 5;

    /**
     * Serialize {@code float} into byte array with following scheme:
     * [{@link #FLOAT_FLAG}] + [float_bytes].
     *
     * @param value target float to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Float value) {
        int val = Float.floatToIntBits(value);
        return new byte[]{
                FLOAT_FLAG,
                (byte) (val >>> 24),
                (byte) (val >>> 16),
                (byte) (val >>> 8),
                (byte) (val)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Float)} convention
     *
     * @param key   token for determinate how to serialize
     *              one type of class type or interface type by two or more
     *              different serialization protocols.
     *              Default key is {@link #EMPTY_KEY}
     * @param bytes target byte array for deserialization
     * @return deserialized float
     */
    @Override
    public Float deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, FLOAT_FLAG);
    }

    /**
     * Deserialize byte by {@link #serialize(Float)} convention
     *
     * @param key    token for determinate how to serialize
     *               one type of class type or interface type by two or more
     *               different serialization protocols.
     *               Default key is {@link #EMPTY_KEY}
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized float
     */
    @Override
    public Float deserialize(String key, byte[] bytes, int offset, int length) {
        int i = 0xFF;
        int value = ((bytes[offset + 4] & i)) +
                ((bytes[offset + 3] & i) << 8) +
                ((bytes[offset + 2] & i) << 16) +
                ((bytes[offset + 1]) << 24);
        return Float.intBitsToFloat(value);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLOAT_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Float;
    }

    @Override
    public int bytesLength() {
        return FLOAT_SIZE;
    }
}