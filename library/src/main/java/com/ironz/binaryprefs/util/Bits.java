package com.ironz.binaryprefs.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Converts bytes to primitives and backwards. For type detecting uses prefix flag which stores at start of byte arrays.
 */
public final class Bits {

    /**
     * Uses for detecting byte array type of String Set
     */
    static final byte FLAG_STRING_SET = -1;
    /**
     * Uses for detecting byte array type of String
     */
    static final byte FLAG_STRING = -2;

    /**
     * Uses for detecting byte array type of int
     */
    static final byte FLAG_INT = -3;
    /**
     * Uses for detecting byte array type of long
     */
    static final byte FLAG_LONG = -4;

    /**
     * Uses for detecting byte array type of float
     */
    static final byte FLAG_FLOAT = -5;

    /**
     * Uses for detecting byte array type of boolean
     */
    static final byte FLAG_BOOLEAN = -6;

    /**
     * Uses for detecting byte array type of byte
     */
    static final byte FLAG_BYTE = -7;

    /**
     * Uses for detecting byte array type of short
     */
    static final byte FLAG_SHORT = -8;

    /**
     * Uses for detecting byte array type of char
     */
    static final byte FLAG_CHAR = -9;

    /**
     * Uses for detecting byte array type of double
     */
    static final byte FLAG_DOUBLE = -10;


    private static final int INITIAL_INTEGER_LENGTH = 5;
    private static final int NULL_STRING_SIZE = -1;

    private Bits() {
    }

    /**
     * Serialize {@code Set<String>} into byte array with following scheme:
     * [{@link #FLAG_STRING_SET}] + (([string_size] + [string_byte_array]) * n).
     *
     * @param value target Set to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] stringSetToBytesWithFlag(Set<String> value) {
        byte[][] bytes = new byte[value.size()][];
        int i = 0;
        int totalArraySize = 1;

        for (String s : value) {
            byte[] stringBytes = s == null ? new byte[0] : s.getBytes();
            byte[] stringSizeBytes = s == null ? intToBytesWithFlag(NULL_STRING_SIZE) : intToBytesWithFlag(stringBytes.length);

            byte[] merged = new byte[stringBytes.length + stringSizeBytes.length];

            System.arraycopy(stringSizeBytes, 0, merged, 0, stringSizeBytes.length);
            System.arraycopy(stringBytes, 0, merged, stringSizeBytes.length, stringBytes.length);

            bytes[i] = merged;

            totalArraySize += merged.length;
            i++;
        }

        byte[] totalArray = new byte[totalArraySize];
        totalArray[0] = FLAG_STRING_SET;

        int offset = 1;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, totalArray, offset, b.length);
            offset = offset + b.length;
        }

        return totalArray;
    }

    /**
     * Deserialize byte by {@link #stringSetToBytesWithFlag(Set)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized String Set
     */
    public static Set<String> stringSetFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG_STRING_SET) {

            if (bytes.length == 1) {
                return new HashSet<>(0);
            }

            Set<String> set = new HashSet<>();

            int i = 1;

            while (i < bytes.length) {

                byte[] stringSizeBytes = {bytes[i], bytes[i + 1], bytes[i + 2], bytes[i + 3], bytes[i + 4]};
                int stringSize = intFromBytesWithFlag(stringSizeBytes);

                if (stringSize == NULL_STRING_SIZE) {
                    set.add(null);
                    i += INITIAL_INTEGER_LENGTH;
                    continue;
                }

                byte[] stringBytes = new byte[stringSize];

                for (int k = 0; k < stringBytes.length; k++) {
                    int offset = i + k + INITIAL_INTEGER_LENGTH;
                    stringBytes[k] = bytes[offset];
                }

                set.add(new String(stringBytes));

                i += INITIAL_INTEGER_LENGTH + stringSize;
            }

            return set;
        }

        throw new ClassCastException(String.format("Set<String> cannot be deserialized in '%s' flag type", flag));
    }

    /**
     * Serialize {@code String} into byte array with following scheme:
     * [{@link #FLAG_STRING}] + [string_byte_array].
     *
     * @param value target String to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] stringToBytesWithFlag(String value) {
        byte[] stringBytes = value.getBytes();
        int flagSize = 1;
        byte[] b = new byte[stringBytes.length + flagSize];
        b[0] = FLAG_STRING;
        System.arraycopy(stringBytes, 0, b, flagSize, stringBytes.length);
        return b;
    }

    /**
     * Deserialize byte by {@link #stringToBytesWithFlag(String)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized String
     */
    public static String stringFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG_STRING) {
            int lengthWithoutFlag = bytes.length - 1;
            return new String(bytes, 1, lengthWithoutFlag);
        }
        throw new ClassCastException(String.format("String cannot be deserialized in '%s' flag type", flag));
    }

    /**
     * Serialize {@code int} into byte array with following scheme:
     * [{@link #FLAG_INT}] + [int_bytes].
     *
     * @param value target int to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] intToBytesWithFlag(int value) {
        return new byte[]{
                FLAG_INT,
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    /**
     * Deserialize byte by {@link #intToBytesWithFlag(int)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized int
     */
    public static int intFromBytesWithFlag(byte[] bytes) {
        int i = 0xFF;
        byte flag = bytes[0];
        if (flag == FLAG_INT) {
            return ((bytes[4] & i)) +
                    ((bytes[3] & i) << 8) +
                    ((bytes[2] & i) << 16) +
                    ((bytes[1]) << 24);
        }
        throw new ClassCastException(String.format("int cannot be deserialized in '%s' flag type", flag));
    }

    /**
     * Serialize {@code float} into byte array with following scheme:
     * [{@link #FLAG_FLOAT}] + [float_bytes].
     *
     * @param value target float to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] floatToBytesWithFlag(float value) {
        int i = Float.floatToIntBits(value);
        return new byte[]{
                FLAG_FLOAT,
                (byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) value
        };
    }

    /**
     * Deserialize byte by {@link #floatToBytesWithFlag(float)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized float
     */
    public static float floatFromBytesWithFlag(byte[] bytes) {
        int i = 0xFF;
        byte flag = bytes[0];
        if (flag == FLAG_FLOAT) {
            int value = ((bytes[4] & i)) +
                    ((bytes[3] & i) << 8) +
                    ((bytes[2] & i) << 16) +
                    (bytes[1] << 24);
            return Float.intBitsToFloat(value);
        }
        throw new ClassCastException(String.format("float cannot be deserialized in '%s' flag type", flag));
    }

    /**
     * Serialize {@code long} into byte array with following scheme:
     * [{@link #FLAG_LONG}] + [long_bytes].
     *
     * @param value target long to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] longToBytesWithFlag(long value) {
        return new byte[]{
                FLAG_LONG,
                (byte) (value >>> 56),
                (byte) (value >>> 48),
                (byte) (value >>> 40),
                (byte) (value >>> 32),
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) (value)
        };
    }

    /**
     * Deserialize byte by {@link #longToBytesWithFlag(long)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized long
     */
    public static long longFromBytesWithFlag(byte[] bytes) {
        long l = 0xFFL;
        byte flag = bytes[0];
        if (flag == FLAG_LONG) {
            return ((bytes[8] & l)) +
                    ((bytes[7] & l) << 8) +
                    ((bytes[6] & l) << 16) +
                    ((bytes[5] & l) << 24) +
                    ((bytes[4] & l) << 32) +
                    ((bytes[3] & l) << 40) +
                    ((bytes[2] & l) << 48) +
                    (((long) bytes[1]) << 56);
        }
        throw new ClassCastException(String.format("long cannot be deserialized in '%s' flag type", flag));
    }

    /**
     * Serialize {@code boolean} into byte array with following scheme:
     * [{@link #FLAG_BOOLEAN}] + [boolean_bytes].
     *
     * @param value target boolean to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] booleanToBytesWithFlag(boolean value) {
        return new byte[]{
                FLAG_BOOLEAN,
                (byte) (value ? 1 : 0)
        };
    }

    /**
     * Deserialize byte by {@link #booleanToBytesWithFlag(boolean)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized boolean
     */
    public static boolean booleanFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG_BOOLEAN) {
            return bytes[1] != 0;
        }
        throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", flag));
    }

    /**
     * Tries to deserialize byte array by all flags and returns object if deserialized or throws exception if target flag is unexpected.
     *
     * @param bytes target byte array for deserialization
     * @return deserialized object
     */
    public static Object tryDeserializeByFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG_STRING_SET) {
            return stringSetFromBytesWithFlag(bytes);
        }
        if (flag == FLAG_STRING) {
            return stringFromBytesWithFlag(bytes);
        }
        if (flag == FLAG_INT) {
            return intFromBytesWithFlag(bytes);
        }
        if (flag == FLAG_LONG) {
            return longFromBytesWithFlag(bytes);
        }
        if (flag == FLAG_FLOAT) {
            return floatFromBytesWithFlag(bytes);
        }
        if (flag == FLAG_BOOLEAN) {
            return booleanFromBytesWithFlag(bytes);
        }
        throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect type '%s'", flag));
    }
}