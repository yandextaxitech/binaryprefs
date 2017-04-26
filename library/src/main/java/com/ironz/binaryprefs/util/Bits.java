package com.ironz.binaryprefs.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Converts bytes to primitives and backwards
 */
@SuppressWarnings("ConstantConditions")
public class Bits {

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

    public static byte[] stringSetToBytes(Set<String> set) {

        byte[][] bytes = new byte[set.size()][];

        int i = 0;
        int totalArraySize = 1;

        for (String s : set) {
            byte[] stringBytes = s == null ? new byte[0] : s.getBytes();
            byte[] stringSizeBytes = s == null ? intToBytes(NULL_STRING_SIZE) : intToBytes(stringBytes.length);

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

    public static Set<String> stringSetFromBytes(byte[] bytes) {

        byte flag = bytes[0];
        if (flag != FLAG_STRING_SET) {
            throw new ClassCastException(String.format("Set<String> cannot be deserialized in '%s' flag type", flag));
        }

        if (bytes.length == 1) {
            return new HashSet<>(0);
        }

        Set<String> set = new HashSet<>();

        int i = 1;

        while (i < bytes.length) {

            byte[] stringSizeBytes = {bytes[i], bytes[i + 1], bytes[i + 2], bytes[i + 3], bytes[i + 4]};
            int stringSize = intFromBytes(stringSizeBytes);

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

    public static byte[] stringToBytes(String s) {
        byte[] bytes = s.getBytes();
        byte[] b = new byte[bytes.length + 1];
        b[0] = FLAG_STRING;
        System.arraycopy(bytes, 0, b, 1, bytes.length);
        return b;
    }

    public static String stringFromBytes(byte[] b) {
        byte flag = b[0];
        if (flag != FLAG_STRING) {
            throw new ClassCastException(String.format("String cannot be deserialized in '%s' flag type", flag));
        }
        return new String(b, 1, b.length - 1);
    }

    public static byte[] intToBytes(int value) {
        return new byte[]{
                FLAG_INT,
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    public static int intFromBytes(byte[] b) {
        int i = 0xFF;
        byte flag = b[0];
        if (flag != FLAG_INT) {
            throw new ClassCastException(String.format("int cannot be deserialized in '%s' flag type", flag));
        }
        return ((b[4] & i)) +
                ((b[3] & i) << 8) +
                ((b[2] & i) << 16) +
                ((b[1]) << 24);
    }

    public static byte[] floatToBytes(float value) {
        int i = Float.floatToIntBits(value);
        return new byte[]{
                FLAG_FLOAT,
                (byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) value
        };
    }

    public static float floatFromBytes(byte[] b) {
        int i = 0xFF;
        byte flag = b[0];
        if (flag != FLAG_FLOAT) {
            throw new ClassCastException(String.format("float cannot be deserialized in '%s' flag type", flag));
        }
        int value = ((b[4] & i)) +
                ((b[3] & i) << 8) +
                ((b[2] & i) << 16) +
                ((b[1]) << 24);
        return Float.intBitsToFloat(value);
    }

    public static byte[] longToBytes(long value) {
        byte[] bytes = new byte[9];
        bytes[8] = (byte) (value);
        bytes[7] = (byte) (value >>> 8);
        bytes[6] = (byte) (value >>> 16);
        bytes[5] = (byte) (value >>> 24);
        bytes[4] = (byte) (value >>> 32);
        bytes[3] = (byte) (value >>> 40);
        bytes[2] = (byte) (value >>> 48);
        bytes[1] = (byte) (value >>> 56);
        bytes[0] = FLAG_LONG;
        return bytes;
    }

    public static long longFromBytes(byte[] b) {
        long l = 0xFFL;
        byte flag = b[0];
        if (flag != FLAG_LONG) {
            throw new ClassCastException(String.format("long cannot be deserialized in '%s' flag type", flag));
        }
        return ((b[8] & l)) +
                ((b[7] & l) << 8) +
                ((b[6] & l) << 16) +
                ((b[5] & l) << 24) +
                ((b[4] & l) << 32) +
                ((b[3] & l) << 40) +
                ((b[2] & l) << 48) +
                (((long) b[1]) << 56);
    }

    public static byte[] booleanToBytes(boolean value) {
        return new byte[]{
                FLAG_BOOLEAN,
                (byte) (value ? 1 : 0)
        };
    }

    public static boolean booleanFromBytes(byte[] b) {
        byte flag = b[0];
        if (flag != FLAG_BOOLEAN) {
            throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", flag));
        }
        return b[1] != 0;
    }
}