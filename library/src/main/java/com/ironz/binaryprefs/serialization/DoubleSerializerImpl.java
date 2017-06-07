package com.ironz.binaryprefs.serialization;


/**
 * Double to byte array implementation of {@link Serializer} and backwards
 */
public final class DoubleSerializerImpl implements Serializer<Double> {

    /**
     * Uses for detecting byte array primitive type of {@link Double}
     */
    private static final byte FLAG_DOUBLE = -5;

    /**
     * Minimum size primitive type of {@link Double}
     */
    private static final int SIZE_DOUBLE = 9;

    /**
     * Serialize {@code double} into byte array with following scheme:
     * [{@link #FLAG_DOUBLE}] + [double_bytes].
     *
     * @param value target double to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Double value) {
        long l = Double.doubleToLongBits(value);
        return new byte[]{
                FLAG_DOUBLE,
                (byte) (l >>> 56),
                (byte) (l >>> 48),
                (byte) (l >>> 40),
                (byte) (l >>> 32),
                (byte) (l >>> 24),
                (byte) (l >>> 16),
                (byte) (l >>> 8),
                (byte) (l)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Double)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized double
     */
    @Override
    public Double deserialize(byte[] bytes) {
        return deserialize(bytes, 0, SIZE_DOUBLE);
    }

    /**
     * Deserialize byte by {@link #serialize(Double)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized double
     */
    @Override
    public Double deserialize(byte[] bytes, int offset, int length) {
        int i = 0xff;
        long value =
                ((bytes[8 + offset] & i)) +
                        ((bytes[7 + offset] & i) << 8) +
                        ((bytes[6 + offset] & i) << 16) +
                        ((long) (bytes[5 + offset] & i) << 24) +
                        ((long) (bytes[4 + offset] & i) << 32) +
                        ((long) (bytes[3 + offset] & i) << 40) +
                        ((long) (bytes[2 + offset] & i) << 48) +
                        ((long) (bytes[1 + offset]) << 56);
        return Double.longBitsToDouble(value);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLAG_DOUBLE;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Double;
    }

    @Override
    public int bytesLength() {
        return SIZE_DOUBLE;
    }
}