package com.ironz.binaryprefs.serialization;

/**
 * Byte to byte array implementation of {@link Serializer} and backwards
 */
public final class ByteSerializerImpl implements Serializer<Byte> {

    /**
     * Uses for detecting byte primitive type of {@link Byte}
     */
    private static final byte FLAG_BYTE = -8;

    /**
     * Minimum size primitive type of {@link Byte}
     */
    private static final int SIZE_BYTE = 2;

    /**
     * Serialize {@code byte} into byte array with following scheme:
     * [{@link #FLAG_BYTE}] + [byte].
     *
     * @param value target byte to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Byte value) {
        return new byte[]{
                FLAG_BYTE,
                value
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Byte)}  convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized byte
     */
    @Override
    public Byte deserialize(byte[] bytes) {
        return deserialize(bytes, 0, SIZE_BYTE);
    }

    /**
     * Deserialize byte by {@link #serialize(Byte)}  convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized byte
     */
    @Override
    public Byte deserialize(byte[] bytes, int offset, int length) {
        return bytes[1 + offset];
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLAG_BYTE;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Byte;
    }

    @Override
    public int bytesLength() {
        return SIZE_BYTE;
    }
}