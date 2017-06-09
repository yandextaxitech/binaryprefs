package com.ironz.binaryprefs.serialization.impl;

/**
 * Long to byte array implementation and backwards
 */
public final class LongSerializer {

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
    public byte[] serialize(long value) {
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
     * Deserialize byte by {@link #serialize(long)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized long
     */
    public long deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #serialize(long)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized long
     */
    public long deserialize(byte[] bytes, int offset) {
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

    public boolean isMatches(byte flag) {
        return flag == LONG_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Long;
    }

    public int bytesLength() {
        return LONG_SIZE;
    }
}