package com.ironz.binaryprefs.serialization;

/**
 * Float to byte array implementation of {@link Serializer} and backwards
 */
public final class FloatSerializerImpl implements Serializer<Float> {

    /**
     * Uses for detecting byte array primitive type of {@link Float}
     */
    private static final byte FLAG_FLOAT = -6;

    /**
     * Minimum size primitive type of {@link Float}
     */
    private static final int SIZE_FLOAT = 5;

    /**
     * Serialize {@code float} into byte array with following scheme:
     * [{@link #FLAG_FLOAT}] + [float_bytes].
     *
     * @param value target float to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Float value) {
        int val = Float.floatToIntBits(value);
        return new byte[]{
                FLAG_FLOAT,
                (byte) (val >>> 24),
                (byte) (val >>> 16),
                (byte) (val >>> 8),
                (byte) (val)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Float)} convention
     *
     * @param key    object token key
     * @param bytes target byte array for deserialization
     * @return deserialized float
     */
    @Override
    public Float deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, FLAG_FLOAT);
    }

    /**
     * Deserialize byte by {@link #serialize(Float)} convention
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized float
     */
    @Override
    public Float deserialize(String key, byte[] bytes, int offset, int length) {
        int i = 0xFF;
        int value = ((bytes[offset + 4] & i)) +
                ((bytes[offset + 3] & i) << 8) +
                ((bytes[offset + 2] & i) << 16) +
                ((bytes[offset + 1]) << 24);
        return Float.intBitsToFloat(value);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == FLAG_FLOAT;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Float;
    }

    @Override
    public int bytesLength() {
        return SIZE_FLOAT;
    }

    @Override
    public byte getFlag() {
        return FLAG_FLOAT;
    }
}