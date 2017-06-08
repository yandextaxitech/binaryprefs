package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Char to byte array implementation of {@link Serializer} and backwards
 */
public final class CharSerializerImpl implements Serializer<Character> {

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
    @Override
    public byte[] serialize(Character value) {
        return new byte[]{
                CHAR_FLAG,
                (byte) (value >>> 8),
                ((byte) ((char) value))
        };
    }

    /**
     * Deserialize char by {@link #serialize(Character)} convention
     *
     * @param key   token for determinate how to serialize
     *              one type of class type or interface type by two or more
     *              different serialization protocols.
     *              Default key is {@link #EMPTY_KEY}
     * @param bytes target byte array for deserialization
     * @return deserialized char
     */
    @Override
    public Character deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, CHAR_SIZE);
    }

    /**
     * Deserialize char by {@link #serialize(Character)} convention
     *
     * @param key    token for determinate how to serialize
     *               one type of class type or interface type by two or more
     *               different serialization protocols.
     *               Default key is {@link #EMPTY_KEY}
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
        return flag == CHAR_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Character;
    }

    @Override
    public int bytesLength() {
        return CHAR_SIZE;
    }
}