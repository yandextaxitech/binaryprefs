package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;

import java.util.HashSet;
import java.util.Set;

/**
 * {@code Set<String>} to byte array implementation of {@link Serializer} and backwards
 */
public final class StringSetSerializerImpl implements Serializer<Set<String>> {

    /**
     * Minimum size primitive type of {@link Set}
     */
    private static final int STRING_SET_SIZE = 1;

    /**
     * Uses for detecting byte array type of {@link Set} of {@link String}
     */
    private static final byte STRING_SET_FLAG = -1;

    /**
     * Serialize {@code Set<String>} into byte array with following scheme:
     * [{@link #STRING_SET_FLAG}] + (([string_size] + [string_byte_array]) * n).
     *
     * @param set target Set to serialize.
     * @return specific byte array with scheme.
     */
    @Override
    public byte[] serialize(Set<String> set) {
        byte[][] bytes = new byte[set.size()][];
        int i = 0;
        int totalArraySize = 1;

        for (String s : set) {
            byte[] stringBytes = s.getBytes();
            byte[] stringSizeBytes = intToBytes(stringBytes.length);

            byte[] merged = new byte[stringBytes.length + stringSizeBytes.length];

            System.arraycopy(stringSizeBytes, 0, merged, 0, stringSizeBytes.length);
            System.arraycopy(stringBytes, 0, merged, stringSizeBytes.length, stringBytes.length);

            bytes[i] = merged;

            totalArraySize += merged.length;
            i++;
        }

        byte[] totalArray = new byte[totalArraySize];
        totalArray[0] = STRING_SET_FLAG;

        int offset = 1;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, totalArray, offset, b.length);
            offset = offset + b.length;
        }

        return totalArray;
    }

    private byte[] intToBytes(int value) {
        int i = 0xff;
        return new byte[]{
                (byte) ((value >>> 24) & i),
                (byte) ((value >>> 16) & i),
                (byte) ((value >>> 8) & i),
                (byte) ((value) & i)
        };
    }

    /**
     * Deserialize byte by {@link #serialize(Set)} convention
     *
     * @param key   object token key
     * @param bytes target byte array for deserialization
     * @return deserialized String Set
     */
    @Override
    public Set<String> deserialize(String key, byte[] bytes) {
        return deserialize(Serializer.EMPTY_KEY, bytes, 0, bytes.length);
    }


    /**
     * Deserialize byte by {@link #serialize(Set)} convention
     *
     * @param key    object token key
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length of bytes array part
     * @return deserialized String Set
     */
    @Override
    public Set<String> deserialize(String key, byte[] bytes, int offset, int length) {
        byte flag = bytes[offset];
        if (flag == STRING_SET_FLAG) {

            Set<String> set = new HashSet<>();

            int i = 1;

            while (i < bytes.length) {

                int integerBytesSize = Integer.SIZE / 8;
                byte[] stringSizeBytes = new byte[integerBytesSize];
                System.arraycopy(bytes, offset + i, stringSizeBytes, 0, stringSizeBytes.length);
                int stringSize = intFromBytes(stringSizeBytes);

                byte[] stringBytes = new byte[stringSize];

                for (int k = 0; k < stringBytes.length; k++) {
                    int stringOffset = offset + i + k + integerBytesSize;
                    stringBytes[k] = bytes[stringOffset];
                }

                set.add(new String(stringBytes));

                i += integerBytesSize + stringSize;
            }

            return set;
        }

        throw new ClassCastException(String.format("Set<String> cannot be deserialized in '%s' flag type", flag));

    }

    private int intFromBytes(byte[] bytes) {
        int i = 0xff;
        return ((bytes[3] & i)) +
                ((bytes[2] & i) << 8) +
                ((bytes[1] & i) << 16) +
                ((bytes[0]) << 24);
    }

    @Override
    public boolean isMatches(byte flag) {
        return flag == STRING_SET_FLAG;
    }

    @Override
    public boolean isMatches(Object o) {
        try {
            //noinspection unused,unchecked
            Set<String> stringSet = (Set<String>) o;
            return true;
        } catch (Exception ignored) {

        }
        return false;
    }

    @Override
    public int bytesLength() {
        return STRING_SET_SIZE;
    }

    @Override
    public byte getFlag() {
        return STRING_SET_FLAG;
    }
}