package com.ironz.binaryprefs.serialization.serializer;

/**
 * Char to byte array implementation and backwards
 */
public final class CharSerializer {

    /**
     * Uses for detecting byte array primitive type of {@link Character}
     */
    private static final byte FLAG = -10;

    /**
     * Minimum size primitive type of {@link Character}
     */
    private static final int SIZE = 3;

    /**
     * Serialize {@code char} into byte array with following scheme:
     * [{@link #FLAG}] + [char_bytes].
     *
     * @param value target char to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(char value) {
        return new byte[]{
                FLAG,
                (byte) (value >>> 8),
                ((byte) ((char) value))
        };
    }

    /**
     * Deserialize {@code char} by {@link #serialize(char)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized char
     */
    public char deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize {@code char} by {@link #serialize(char)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized char
     */
    public char deserialize(byte[] bytes, int offset) {
        int i = 0xFF;
        return (char) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
    }

    public boolean isMatches(byte flag) {
        return flag == FLAG;
    }

    public int bytesLength() {
        return SIZE;
    }
}