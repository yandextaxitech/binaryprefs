package com.ironz.binaryprefs.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Converts bytes to primitives and backwards
 */
public final class Bits {

    static final byte FLAG_STRING_SET = -10;
    static final byte FLAG_STRING = -20;
    static final byte FLAG_INT = -30;
    static final byte FLAG_LONG = -40;
    static final byte FLAG_FLOAT = -50;
    static final byte FLAG_BOOLEAN = -60;

    private static final int INITIAL_INTEGER_LENGTH = 5;
    private static final int NULL_STRING_SIZE = -1;

    private Bits() {
    }

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

    public static byte[] stringToBytesWithFlag(String value) {
        byte[] stringBytes = value.getBytes();
        int flagSize = 1;
        byte[] b = new byte[stringBytes.length + flagSize];
        b[0] = FLAG_STRING;
        System.arraycopy(stringBytes, 0, b, flagSize, stringBytes.length);
        return b;
    }

    public static String stringFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG_STRING) {
            int lengthWithoutFlag = bytes.length - 1;
            return new String(bytes, 1, lengthWithoutFlag);
        }
        throw new ClassCastException(String.format("String cannot be deserialized in '%s' flag type", flag));
    }

    public static byte[] intToBytesWithFlag(int value) {
        return new byte[]{
                FLAG_INT,
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

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

    public static byte[] booleanToBytesWithFlag(boolean value) {
        return new byte[]{
                FLAG_BOOLEAN,
                (byte) (value ? 1 : 0)
        };
    }

    public static boolean booleanFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag == FLAG_BOOLEAN) {
            return bytes[1] != 0;
        }
        throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", flag));
    }

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