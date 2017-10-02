package com.ironz.binaryprefs.serialization.serializer;

/**
 * Byte to byte array implementation and backwards
 */
public final class ByteSerializer {

    /**
     * Uses for detecting byte primitive type of {@link Byte}
     */
    private static final byte FLAG = -8;

    /**
     * Minimum size primitive type of {@link Byte}
     */
    private static final int SIZE = 2;

    /**
     * Serialize {@code byte} into byte array with following scheme:
     * [{@link #FLAG}] + [byte].
     *
     * @param value target byte to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(byte value) {
        return new byte[]{
                FLAG,
                value
        };
    }

    /**
     * Deserialize {@code byte} by {@link #serialize(byte)}  convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized byte
     */
    public byte deserialize(byte[] bytes) {
        return deserialize(bytes, 0);
    }

    /**
     * Deserialize {@code byte} by {@link #serialize(byte)}  convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset bytes array offset
     * @return deserialized byte
     */
    public byte deserialize(byte[] bytes, int offset) {
        return bytes[offset + 1];
    }

    public boolean isMatches(byte flag) {
        return flag == FLAG;
    }

    public int bytesLength() {
        return SIZE;
    }
}