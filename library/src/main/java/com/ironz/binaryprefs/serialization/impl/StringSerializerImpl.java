package com.ironz.binaryprefs.serialization.impl;

/**
 * {@code String} to byte array implementation and backwards
 */
public final class StringSerializerImpl {

    /**
     * Uses for detecting byte array type of {@link String}
     */
    public static final byte STRING_FLAG = (byte) -2;

    /**
     * Minimum size primitive type of {@link String}
     */
    private static final int STRING_SIZE = 1;

    /**
     * Serialize {@code String} into byte array with following scheme:
     * [{@link #STRING_FLAG}] + [string_byte_array].
     *
     * @param s target String to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(String s) {
        byte[] stringBytes = s.getBytes();
        int flagSize = 1;
        byte[] b = new byte[stringBytes.length + flagSize];
        b[0] = STRING_FLAG;
        System.arraycopy(stringBytes, 0, b, flagSize, stringBytes.length);
        return b;
    }

    /**
     * Deserialize byte by {@link #serialize(String)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized String
     */
    public String deserialize(byte[] bytes) {
        int flagOffset = 1;
        return new String(bytes, flagOffset, bytes.length - 1);
    }

    public boolean isMatches(byte flag) {
        return flag == STRING_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof String;
    }

    public int bytesLength() {
        return STRING_SIZE;
    }
}