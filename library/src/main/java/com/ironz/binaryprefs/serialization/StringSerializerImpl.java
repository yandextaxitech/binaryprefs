package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.persistable.Persistable;

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
     * @param bytes target byte array for deserialization
     * @return deserialized String
     */
    @Override
    public String deserialize(byte[] bytes) {
        return deserialize(Persistable.EMPTY_KEY, bytes, 0, bytes.length);
    }

    /**
     * Deserialize byte by {@link #serialize(String)} convention
     *
     *
     * @param key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized String
     */
    @Override
    public String deserialize(String key, byte[] bytes, int offset, int length) {
        int flagOffset = 1;
        return new String(bytes, offset + flagOffset, length - 1);
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
}