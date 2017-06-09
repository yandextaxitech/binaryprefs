package com.ironz.binaryprefs.serialization.impl;

/**
 * Float to byte array implementation and backwards
 */
public final class FloatSerializer {

    /**
     * Uses for detecting byte array primitive type of {@link Float}
     */
    private static final byte FLOAT_FLAG = -6;

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
    public byte[] serialize(float value) {
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
     * Deserialize byte by {@link #serialize(float)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized float
     */
    public float deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #serialize(float)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized float
     */
    public float deserialize(byte[] bytes, int offset) {
        int i = 0xFF;
        int value = ((bytes[4 + offset] & i)) +
                ((bytes[3 + offset] & i) << 8) +
                ((bytes[2 + offset] & i) << 16) +
                ((bytes[1 + offset]) << 24);
        return Float.intBitsToFloat(value);
    }

    public boolean isMatches(byte flag) {
        return flag == FLOAT_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Float;
    }

    public int bytesLength() {
        return FLOAT_SIZE;
    }
}