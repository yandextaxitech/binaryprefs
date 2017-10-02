package com.ironz.binaryprefs.serialization.serializer;

/**
 * Boolean to byte array implementation of and backwards
 */
public final class BooleanSerializer {

    /**
     * Uses for detecting byte array primitive type of {@link Boolean}
     */
    private static final byte FLAG = -7;

    /**
     * Minimum size primitive type of {@link Boolean}
     */
    private static final int SIZE = 2;

    /**
     * Serialize {@code boolean} into byte array with following scheme:
     * [{@link #FLAG}] + [boolean_bytes].
     *
     * @param value target boolean to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(boolean value) {
        return new byte[]{
                FLAG,
                (byte) (value ? 1 : 0)
        };
    }

    /**
     * Deserialize {@code boolean} by {@link #serialize(boolean)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized boolean
     */
    public boolean deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize {@code String} by {@link #serialize(boolean)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset byte array offset
     * @return deserialized boolean
     */
    public boolean deserialize(byte[] bytes, int offset) {
        return bytes[1 + offset] != 0;
    }

    public boolean isMatches(byte flag) {
        return flag == FLAG;
    }

    public int bytesLength() {
        return SIZE;
    }
}