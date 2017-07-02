package com.ironz.binaryprefs.serialization.serializer;

import java.util.Collection;
import java.util.HashSet;

/**
 * {@code Collection<String>} to byte array implementation and backwards
 */
public final class CollectionStringSerializer {

    /**
     * Uses for detecting byte array type of {@link Collection} of {@link String}
     */
    private static final byte FLAG = -1;

    /**
     * Minimum size primitive type of {@link Collection}
     */
    private static final int SIZE = 1;

    /**
     * Serialize {@code Collection<String>} into byte array with following scheme:
     * [{@link #FLAG}] + (([string_size] + [string_byte_array]) * n).
     *
     * @param collection target Collection to serialize.
     * @return specific byte array with scheme.
     */
    public byte[] serialize(Collection<String> collection) {
        byte[][] bytes = new byte[collection.size()][];
        int i = 0;
        int totalArraySize = 1;

        for (String s : collection) {
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
        totalArray[0] = FLAG;

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
     * Deserialize byte by {@link #serialize(Collection)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized String Collection
     */
    public Collection<String> deserialize(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG) {

            Collection<String> collection = new HashSet<>();

            int i = 1;

            while (i < bytes.length) {

                int integerBytesSize = Integer.SIZE / 8;
                byte[] stringSizeBytes = new byte[integerBytesSize];
                System.arraycopy(bytes, i, stringSizeBytes, 0, stringSizeBytes.length);
                int stringSize = intFromBytes(stringSizeBytes);

                byte[] stringBytes = new byte[stringSize];

                for (int k = 0; k < stringBytes.length; k++) {
                    int stringOffset = i + k + integerBytesSize;
                    stringBytes[k] = bytes[stringOffset];
                }

                collection.add(new String(stringBytes));

                i += integerBytesSize + stringSize;
            }

            return collection;
        }

        throw new ClassCastException(String.format("Collection<String> cannot be deserialized in '%s' flag type", flag));
    }

    Collection<String> deserializeWithCollectionClass(byte[] bytes, Class<? extends Collection> collectionClass) throws IllegalAccessException, InstantiationException {
        byte flag = bytes[0];
        if (flag == FLAG) {

            Collection<String> collection = collectionClass.newInstance();

            int i = 1;

            while (i < bytes.length) {

                int integerBytesSize = Integer.SIZE / 8;
                byte[] stringSizeBytes = new byte[integerBytesSize];
                System.arraycopy(bytes, i, stringSizeBytes, 0, stringSizeBytes.length);
                int stringSize = intFromBytes(stringSizeBytes);

                byte[] stringBytes = new byte[stringSize];

                for (int k = 0; k < stringBytes.length; k++) {
                    int stringOffset = i + k + integerBytesSize;
                    stringBytes[k] = bytes[stringOffset];
                }

                collection.add(new String(stringBytes));

                i += integerBytesSize + stringSize;
            }

            return collection;
        }

        throw new ClassCastException(String.format("Collection<String> cannot be deserialized in '%s' flag type", flag));
    }


    private int intFromBytes(byte[] bytes) {
        int i = 0xff;
        return ((bytes[3] & i)) +
                ((bytes[2] & i) << 8) +
                ((bytes[1] & i) << 16) +
                ((bytes[0]) << 24);
    }

    public boolean isMatches(byte flag) {
        return flag == FLAG;
    }

    public int bytesLength() {
        return SIZE;
    }
}