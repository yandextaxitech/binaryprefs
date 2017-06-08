package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

/**
 * Short to byte array implementation of {@link Serializer} and backwards
 */
public final class ShortSerializerImpl implements Serializer<Short> {

    /**
     * Uses for detecting byte array primitive type of {@link Short}
     */
    private static final byte SHORT_FLAG = -9;

    /**
     * Minimum size primitive type of {@link Short}
     */
    private static final int SHORT_SIZE = 3;

    /**
     * Serialize {@code short} into byte array with following scheme:
     * [{@link #SHORT_FLAG}] + [short_bytes].
     *
     * @param value target short to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Short value) {
        return new byte[]{
                SHORT_FLAG,
                (byte) (value >>> 8),
                ((byte) ((short) value))
        };
    }

    /**
     * Deserialize short by {@link #serialize(Short)}  convention
     *
     * @param key    object token key
     * @param bytes target byte array for deserialization
     * @return deserialized short
     */
    @Override
    public Short deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, SHORT_SIZE);
    }

    /**
     * Deserialize short by {@link #serialize(Short)}  convention
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized short
     */
    @Override
    public Short deserialize(String key, byte[] bytes, int offset, int length) {
        int i = 0xff;
        return (short) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == SHORT_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        return o instanceof Short;
    }

    @Override
    public int bytesLength() {
        return SHORT_SIZE;
    }

    @Override
    public byte getFlag() {
        return SHORT_FLAG;
    }
}