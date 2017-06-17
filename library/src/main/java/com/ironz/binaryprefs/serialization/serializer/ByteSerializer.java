package com.ironz.binaryprefs.serialization.serializer;

/**
 * Byte to byte array implementation and backwards
 */
public final class ByteSerializer {

    /**
     * Uses for detecting byte primitive type of {@link Byte}
     */
    private static final byte BYTE_FLAG = -8;

    /**
     * Minimum size primitive type of {@link Byte}
     */
    private static final int BYTE_SIZE = 2;

    /**
     * Serialize {@code byte} into byte array with following scheme:
     * [{@link #BYTE_FLAG}] + [byte].
     *
     * @param value target byte to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(byte value) {
        return new byte[]{
                BYTE_FLAG,
                value
        };
    }

    /**
     * Deserialize byte by {@link #serialize(byte)}  convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized byte
     */
    public byte deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #serialize(byte)}  convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized byte
     */
    public byte deserialize(byte[] bytes, int offset) {
        return bytes[offset + 1];
    }

    public boolean isMatches(byte flag) {
        return flag == BYTE_FLAG;
    }

    public boolean isMatches(Object o) {
        return o instanceof Byte;
    }

    public int bytesLength() {
        return BYTE_SIZE;
    }
}