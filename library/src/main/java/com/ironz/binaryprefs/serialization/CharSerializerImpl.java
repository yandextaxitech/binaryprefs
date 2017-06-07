package com.ironz.binaryprefs.serialization;

/**
 * Char to byte array implementation of {@link Serializer} and backwards
 */
public final class CharSerializerImpl implements Serializer<Character> {

    /**
     * Uses for detecting byte array primitive type of {@link Character}
     */
    private static final byte FLAG_CHAR = -10;

    /**
     * Minimum size primitive type of {@link Character}
     */
    private static final int SIZE_CHAR = 3;

    /**
     * Serialize {@code char} into byte array with following scheme:
     * [{@link #FLAG_CHAR}] + [char_bytes].
     *
     * @param value target char to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Character value) {
        return new byte[]{
                FLAG_CHAR,
                (byte) (value >>> 8),
                ((byte) ((char) value))
        };
    }

    /**
     * Deserialize char by {@link #serialize(Character)} convention
     *
     * @param key    object token key
     * @param bytes target byte array for deserialization
     * @return deserialized char
     */
    @Override
    public Character deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, SIZE_CHAR);
    }

    /**
     * Deserialize char by {@link #serialize(Character)} convention
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized char
     */
    @Override
    public Character deserialize(String key, byte[] bytes, int offset, int length) {
        int i = 0xFF;
        return (char) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLAG_CHAR;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Character;
    }

    @Override
    public int bytesLength() {
        return SIZE_CHAR;
    }

    @Override
    public byte getFlag() {
        return FLAG_CHAR;
    }
}