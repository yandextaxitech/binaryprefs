package com.ironz.binaryprefs.serialization.impl;

/**
 * Boolean to byte array implementation of and backwards
 */
public final class BooleanSerializer {

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
    public byte[] serialize(boolean value) {
        return new byte[]{
                BOOLEAN_FLAG,
                (byte) (value ? 1 : 0)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(boolean)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized boolean
     */
    public boolean deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #serialize(boolean)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset byte array offset
     * @return deserialized boolean
     */
    public boolean deserialize(byte[] bytes, int offset) {
        return bytes[1 + offset] != 0;
    }

    public boolean isMatches(byte flag) {
        return flag == BOOLEAN_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Boolean;
    }

    public int bytesLength() {
        return BOOLEAN_SIZE;
    }
}