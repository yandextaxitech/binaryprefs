package com.ironz.binaryprefs.serialization.impl;

/**
 * Char to byte array implementation and backwards
 */
public final class CharSerializerImpl {

    /**
     * Uses for detecting byte array primitive type of {@link Character}
     */
    public static final byte CHAR_FLAG = -10;

    /**
     * Minimum size primitive type of {@link Character}
     */
    private static final int CHAR_SIZE = 3;

    /**
     * Serialize {@code char} into byte array with following scheme:
     * [{@link #CHAR_FLAG}] + [char_bytes].
     *
     * @param value target char to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(char value) {
        return new byte[]{
                CHAR_FLAG,
                (byte) (value >>> 8),
                ((byte) ((char) value))
        };
    }

    /**
     * Deserialize char by {@link #serialize(char)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized char
     */
    public char deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize char by {@link #serialize(char)} convention
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
        return flag == CHAR_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Character;
    }

    public int bytesLength() {
        return CHAR_SIZE;
    }
}