package com.ironz.binaryprefs.serialization;

/**
 * Short to byte array implementation of {@link Serializer} and backwards
 */
public final class ShortSerializerImpl implements Serializer<Short> {

    /**
     * Uses for detecting byte array primitive type of {@link Short}
     */
    private static final byte FLAG_SHORT = -9;

    /**
     * Minimum size primitive type of {@link Short}
     */
    private static final int SIZE_SHORT = 3;

    /**
     * Serialize {@code short} into byte array with following scheme:
     * [{@link #FLAG_SHORT}] + [short_bytes].
     *
     * @param value target short to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Short value) {
        return new byte[]{
                FLAG_SHORT,
                (byte) (value >>> 8),
                ((byte) ((short) value))
        };
    }

    /**
     * Deserialize short by {@link #serialize(Short)}  convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized short
     */
    @Override
    public Short deserialize(byte[] bytes) {
        return deserialize(bytes, 0, SIZE_SHORT);
    }

    /**
     * Deserialize short by {@link #serialize(Short)}  convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized short
     */
    @Override
    public Short deserialize(byte[] bytes, int offset, int length) {
        int i = 0xff;
        return (short) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLAG_SHORT;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Short;
    }

    @Override
    public int bytesLength() {
        return SIZE_SHORT;
    }
}