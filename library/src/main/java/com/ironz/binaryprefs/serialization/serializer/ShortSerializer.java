package com.ironz.binaryprefs.serialization.serializer;

/**
 * Short to byte array implementation and backwards
 */
public final class ShortSerializer {

    /**
     * Uses for detecting byte array primitive type of {@link Short}
     */
    private static final byte FLAG = -9;

    /**
     * Minimum size primitive type of {@link Short}
     */
    private static final int SIZE = 3;

    /**
     * Serialize {@code short} into byte array with following scheme:
     * [{@link #FLAG}] + [short_bytes].
     *
     * @param value target short to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(short value) {
        return new byte[]{
                FLAG,
                (byte) (value >>> 8),
                ((byte) ((short) value))
        };
    }

    /**
     * Deserialize {@code short} by {@link #serialize(short)}  convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized short
     */
    public short deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize {@code short} by {@link #serialize(short)}  convention
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
        return flag == FLAG;
    }

    public int bytesLength() {
        return SIZE;
    }
}