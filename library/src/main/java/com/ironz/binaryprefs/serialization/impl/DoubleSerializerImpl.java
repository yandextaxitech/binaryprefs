package com.ironz.binaryprefs.serialization.impl;


import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Double to byte array implementation of {@link Serializer} and backwards
 */
public final class DoubleSerializerImpl implements Serializer<Double> {

    /**
     * Uses for detecting byte array primitive type of {@link Double}
     */
    public static final byte DOUBLE_FLAG = -5;

    /**
     * Minimum size primitive type of {@link Double}
     */
    private static final int DOUBLE_SIZE = 9;

    /**
     * Serialize {@code double} into byte array with following scheme:
     * [{@link #DOUBLE_FLAG}] + [double_bytes].
     *
     * @param value target double to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Double value) {
        long l = Double.doubleToLongBits(value);
        return new byte[]{
                DOUBLE_FLAG,
                (byte) (l >>> 56),
                (byte) (l >>> 48),
                (byte) (l >>> 40),
                (byte) (l >>> 32),
                (byte) (l >>> 24),
                (byte) (l >>> 16),
                (byte) (l >>> 8),
                (byte) (l)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Double)} convention
     *
     * @param key   token for determinate how to serialize
     *              one type of class type or interface type by two or more
     *              different serialization protocols.
     *              Default key is {@link #EMPTY_KEY}
     * @param bytes target byte array for deserialization
     * @return deserialized double
     */
    @Override
    public Double deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, DOUBLE_SIZE);
    }

    /**
     * Deserialize byte by {@link #serialize(Double)} convention
     *
     * @param key    token for determinate how to serialize
     *               one type of class type or interface type by two or more
     *               different serialization protocols.
     *               Default key is {@link #EMPTY_KEY}
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized double
     */
    @Override
    public Double deserialize(String key, byte[] bytes, int offset, int length) {
        int i = 0xff;
        long value =
                ((bytes[8 + offset] & i)) +
                        ((bytes[7 + offset] & i) << 8) +
                        ((bytes[6 + offset] & i) << 16) +
                        ((long) (bytes[5 + offset] & i) << 24) +
                        ((long) (bytes[4 + offset] & i) << 32) +
                        ((long) (bytes[3 + offset] & i) << 40) +
                        ((long) (bytes[2 + offset] & i) << 48) +
                        ((long) (bytes[1 + offset]) << 56);
        return Double.longBitsToDouble(value);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == DOUBLE_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Double;
    }

    @Override
    public int bytesLength() {
        return DOUBLE_SIZE;
    }
}