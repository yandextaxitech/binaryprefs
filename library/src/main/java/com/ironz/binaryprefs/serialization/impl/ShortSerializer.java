package com.ironz.binaryprefs.serialization.impl;

/**
 * Short to byte array implementation and backwards
 */
public final class ShortSerializer {

    /**
     * Uses for detecting byte array primitive type of {@link Short}
     */
    public static final byte SHORT_FLAG = -9;

    /**
     * Minimum size primitive type of {@link Short}
     */
    private static final int SHORT_SIZE = 3;

    /**
     * Serialize {@code short} into byte array with following scheme:
     * [{@link #SHORT_FLAG}] + [short_bytes].
     *
     * @param value target short to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(short value) {
        return new byte[]{
                SHORT_FLAG,
                (byte) (value >>> 8),
                ((byte) ((short) value))
        };
    }

    /**
     * Deserialize short by {@link #serialize(short)}  convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized short
     */
    public short deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize short by {@link #serialize(short)}  convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized short
     */
    public short deserialize(byte[] bytes, int offset) {
        int i = 0xff;
        return (short) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
    }

    public boolean isMatches(byte flag) {
        return flag == SHORT_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Short;
    }

    public int bytesLength() {
        return SHORT_SIZE;
    }
}