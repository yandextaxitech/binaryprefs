package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * {@code String} to byte array implementation of {@link Serializer} and backwards
 */
public final class StringSerializerImpl implements Serializer<String> {

    /**
     * Uses for detecting byte array type of {@link String}
     */
    private static final byte FLAG_STRING = (byte) -2;

    /**
     * Minimum size primitive type of {@link String}
     */
    private static final int SIZE_STRING = 1;

    /**
     * Serialize {@code String} into byte array with following scheme:
     * [{@link #FLAG_STRING}] + [string_byte_array].
     *
     * @param s target String to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(String s) {
        byte[] stringBytes = s.getBytes();
        int flagSize = 1;
        byte[] b = new byte[stringBytes.length + flagSize];
        b[0] = FLAG_STRING;
        System.arraycopy(stringBytes, 0, b, flagSize, stringBytes.length);
        return b;
    }

    /**
     * Deserialize byte by {@link #serialize(String)} convention
     *
     * @param key    object token key
     * @param bytes target byte array for deserialization
     * @return deserialized String
     */
    @Override
    public String deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, bytes.length - 1);
    }

    /**
     * Deserialize byte by {@link #serialize(String)} convention
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized String
     */
    @Override
    public String deserialize(String key, byte[] bytes, int offset, int length) {
        int flagOffset = 1;
        return new String(bytes, offset + flagOffset, length);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLAG_STRING;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof String;
    }

    @Override
    public int bytesLength() {
        return SIZE_STRING;
    }

    @Override
    public byte getFlag() {
        return FLAG_STRING;
    }
}