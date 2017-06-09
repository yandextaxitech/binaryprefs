package com.ironz.binaryprefs.serialization.impl;

/**
 * Integer to byte array implementation and backwards
 */
public final class IntegerSerializerImpl {

    /**
     * Minimum size primitive type of {@link Integer}
     */
    private static final int INT_SIZE = 5;

    /**
     * Uses for detecting byte array primitive type of {@link Integer}
     */
    public static final byte INT_FLAG = (byte) -3;

    /**
     * Serialize {@code int} into byte array with following scheme:
     * [{@link #INT_FLAG}] + [int_bytes].
     *
     * @param value target int to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(int value) {
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
     * Deserialize byte by {@link #serialize(int)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized int
     */
    public int deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #serialize(int)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized int
     */
    public int deserialize(byte[] bytes, int offset) {
        int i = 0xff;
        return ((bytes[4 + offset] & i)) +
                ((bytes[3 + offset] & i) << 8) +
                ((bytes[2 + offset] & i) << 16) +
                ((bytes[1 + offset]) << 24);
    }

    public boolean isMatches(byte flag) {
        return flag == INT_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Integer;
    }

    public int bytesLength() {
        return INT_SIZE;
    }
}