package com.ironz.binaryprefs.serialization.serializer;

/**
 * {@code String} to byte array implementation and backwards
 */
public final class StringSerializer {

    /**
     * Uses for detecting byte array type of {@link String}
     */
    private static final byte FLAG = -2;
    /**
     * Minimum size primitive type of {@link String}
     */
    private static final int SIZE = 1;

    /**
     * Describes flag offset size
     */
    private static final int FLAG_OFFSET = 1;

    /**
     * Serialize {@code String} into byte array with following scheme:
     * [{@link #FLAG}] + [string_byte_array].
     *
     * @param s target String to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(String s) {
        byte[] stringBytes = s.getBytes();
        byte[] b = new byte[stringBytes.length + FLAG_OFFSET];
        b[0] = FLAG;
        System.arraycopy(stringBytes, 0, b, FLAG_OFFSET, stringBytes.length);
        return b;
    }

    /**
     * Deserialize {@link String} by {@link #serialize(String)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized String
     */
    public String deserialize(byte[] bytes) {
        return deserialize(bytes, 0, bytes.length - 1);
    }

    /**
     * Deserialize {@link String} by {@link #serialize(String)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @param length bytes array length
     * @return deserialized String
     */
    public String deserialize(byte[] bytes, int offset, int length) {
        return new String(bytes, FLAG_OFFSET + offset, length);
    }

    public boolean isMatches(byte flag) {
        return flag == FLAG;
    }

    public int bytesLength() {
        return SIZE;
    }
}