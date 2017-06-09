package com.ironz.binaryprefs.serialization.impl;


/**
 * Double to byte array implementation and backwards
 */
public final class DoubleSerializerImpl {

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
    public byte[] serialize(double value) {
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
     * Deserialize byte by {@link #serialize(double)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized double
     */
    public double deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #serialize(double)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized double
     */
    public double deserialize(byte[] bytes, int offset) {
        int i = 0xff;
        long value = ((bytes[8 + offset] & i)) +
                ((bytes[7 + offset] & i) << 8) +
                ((bytes[6 + offset] & i) << 16) +
                ((long) (bytes[5 + offset] & i) << 24) +
                ((long) (bytes[4 + offset] & i) << 32) +
                ((long) (bytes[3 + offset] & i) << 40) +
                ((long) (bytes[2 + offset] & i) << 48) +
                ((long) (bytes[1 + offset]) << 56);
        return Double.longBitsToDouble(value);
    }

    public boolean isMatches(byte flag) {
        return flag == DOUBLE_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Double;
    }

    public int bytesLength() {
        return DOUBLE_SIZE;
    }
}