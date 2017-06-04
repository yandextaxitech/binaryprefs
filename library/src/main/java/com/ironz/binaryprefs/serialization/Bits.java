package com.ironz.binaryprefs.serialization;

import java.util.HashSet;
import java.util.Set;

/**
 * Converts bytes to primitives and backwards. For type detecting uses prefix flag which stores at start of byte arrays.
 */
public final class Bits {

    /**
     * Uses for detecting byte array type of {@link Set} of {@link String}
     */
    static final byte FLAG_STRING_SET = -1;

    /**
     * Uses for detecting byte array type of {@link String}
     */
    static final byte FLAG_STRING = -2;

    /**
     * Uses for detecting byte array primitive type of {@link Integer}
     */
    static final byte FLAG_INT = -3;

    /**
     * Uses for detecting byte array primitive type of {@link Long}
     */
    static final byte FLAG_LONG = -4;

    /**
     * Uses for detecting byte array primitive type of {@link Double}
     */
    static final byte FLAG_DOUBLE = -5;

    /**
     * Uses for detecting byte array primitive type of {@link Float}
     */
    static final byte FLAG_FLOAT = -6;

    /**
     * Uses for detecting byte array primitive type of {@link Boolean}
     */
    static final byte FLAG_BOOLEAN = -7;

    /**
     * Uses for detecting byte primitive type of {@link Byte}
     */
    static final byte FLAG_BYTE = -8;

    /**
     * Uses for detecting byte array primitive type of {@link Byte}
     */
    static final byte FLAG_BYTE_ARRAY = -9;

    /**
     * Uses for detecting byte array primitive type of {@link Short}
     */
    static final byte FLAG_SHORT = -10;

    /**
     * Uses for detecting byte array primitive type of {@link Character}
     */
    static final byte FLAG_CHAR = -11;

    /**
     * Uses for detecting byte array primitive type of {@link java.io.Externalizable}
     */
    static final byte FLAG_EXTERNALIZABLE = -12;

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
     * [{@link #FLAG_STRING}] + [string_length_int] + [string_byte_array].
     *
     * @param value target String to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] stringToBytesWithFlag(String value) {

        byte[] stringBytes = value.getBytes();
        int flagSize = 1;
        int stringLength = stringBytes.length;
        byte[] lengthBytes = intToBytesWithFlag(stringLength);
        int mainOffset = flagSize + lengthBytes.length;

        byte[] bytes = new byte[flagSize + lengthBytes.length + stringLength];

        bytes[0] = FLAG_STRING;

        System.arraycopy(lengthBytes, 0, bytes, flagSize, lengthBytes.length);
        System.arraycopy(stringBytes, 0, bytes, mainOffset, stringBytes.length);

        return bytes;
    }

    /**
     * Deserialize byte by {@link #stringToBytesWithFlag(String)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized String
     */
    public static String stringFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag != FLAG_STRING) {
            throw new ClassCastException(String.format("String cannot be deserialized in '%s' flag type", flag));
        }

        int flagSize = 1;
        byte[] stringLengthBytes = new byte[INITIAL_INTEGER_LENGTH];

        System.arraycopy(bytes, flagSize, stringLengthBytes, 0, stringLengthBytes.length);

        int mainOffset = flagSize + stringLengthBytes.length;
        int expectedStringLength = intFromBytesWithFlag(stringLengthBytes);

        return new String(bytes, mainOffset, expectedStringLength);
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
        if (flag != FLAG_INT) {
            throw new ClassCastException(String.format("int cannot be deserialized in '%s' flag type", flag));
        }
        return ((bytes[4] & i)) +
                ((bytes[3] & i) << 8) +
                ((bytes[2] & i) << 16) +
                ((bytes[1]) << 24);
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
        if (flag != FLAG_LONG) {
            throw new ClassCastException(String.format("long cannot be deserialized in '%s' flag type", flag));
        }
        return ((bytes[8] & l)) +
                ((bytes[7] & l) << 8) +
                ((bytes[6] & l) << 16) +
                ((bytes[5] & l) << 24) +
                ((bytes[4] & l) << 32) +
                ((bytes[3] & l) << 40) +
                ((bytes[2] & l) << 48) +
                (((long) bytes[1]) << 56);
    }

    /**
     * Serialize {@code double} into byte array with following scheme:
     * [{@link #FLAG_DOUBLE}] + [double_bytes].
     *
     * @param value target double to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] doubleToBytesWithFlag(double value) {
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
                (byte) (value)
        };
    }

    /**
     * Deserialize byte by {@link #doubleToBytesWithFlag(double)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized double
     */
    public static double doubleFromBytesWithFlag(byte[] bytes) {
        long l = 0xFFL;
        byte flag = bytes[0];
        if (flag != FLAG_DOUBLE) {
            throw new ClassCastException(String.format("double cannot be deserialized in '%s' flag type", flag));
        }
        long value = ((bytes[8] & l)) +
                ((bytes[7] & l) << 8) +
                ((bytes[6] & l) << 16) +
                ((bytes[5] & l) << 24) +
                ((bytes[4] & l) << 32) +
                ((bytes[3] & l) << 40) +
                ((bytes[2] & l) << 48) +
                (((long) bytes[1]) << 56);
        return Double.longBitsToDouble(value);
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
        if (flag != FLAG_FLOAT) {
            throw new ClassCastException(String.format("float cannot be deserialized in '%s' flag type", flag));
        }
        int value = ((bytes[4] & i)) +
                ((bytes[3] & i) << 8) +
                ((bytes[2] & i) << 16) +
                (bytes[1] << 24);
        return Float.intBitsToFloat(value);
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
        if (flag != FLAG_BOOLEAN) {
            throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", flag));
        }
        return bytes[1] != 0;
    }

    /**
     * Serialize {@code byte} into byte array with following scheme:
     * [{@link #FLAG_BYTE}] + [byte].
     *
     * @param value target byte to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] byteToBytesWithFlag(byte value) {
        return new byte[]{
                FLAG_BYTE,
                value
        };
    }

    /**
     * Serialize {@code byte array} into byte array with following scheme:
     * [{@link #FLAG_BYTE}] + [byte_array].
     *
     * @param value target byte array to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] byteArrayToBytesWithFlag(byte[] value) {
        int flagOffset = 1;
        byte[] copy = new byte[value.length + flagOffset];
        copy[0] = FLAG_BYTE_ARRAY;
        System.arraycopy(value, 0, copy, flagOffset, value.length);
        return copy;
    }

    /**
     * Deserialize byte array by {@link #byteToBytesWithFlag(byte)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized byte array
     */
    public static byte[] byteArrayFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag != FLAG_BYTE_ARRAY) {
            throw new ClassCastException(String.format("byte array cannot be deserialized in '%s' flag type", flag));
        }
        int flagOffset = 1;
        byte[] copy = new byte[bytes.length - flagOffset];
        System.arraycopy(bytes, flagOffset, copy, 0, copy.length);
        return copy;
    }

    /**
     * Deserialize byte by {@link #byteToBytesWithFlag(byte)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized byte
     */
    public static byte byteFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag != FLAG_BYTE) {
            throw new ClassCastException(String.format("byte cannot be deserialized in '%s' flag type", flag));
        }
        return bytes[1];
    }

    /**
     * Serialize {@code short} into byte array with following scheme:
     * [{@link #FLAG_SHORT}] + [short_bytes].
     *
     * @param value target short to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] shortToBytesWithFlag(short value) {
        return new byte[]{
                FLAG_SHORT,
                (byte) (value >>> 8),
                ((byte) value)
        };
    }

    /**
     * Deserialize short by {@link #shortToBytesWithFlag(short)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized short
     */
    public static short shortFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag != FLAG_SHORT) {
            throw new ClassCastException(String.format("short cannot be deserialized in '%s' flag type", flag));
        }
        return (short) ((bytes[1] << 8) +
                (bytes[2] & 0xFF));
    }

    /**
     * Serialize {@code char} into byte array with following scheme:
     * [{@link #FLAG_CHAR}] + [char_bytes].
     *
     * @param value target char to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] charToBytesWithFlag(char value) {
        return new byte[]{
                FLAG_CHAR,
                (byte) (value >>> 8),
                ((byte) value)
        };
    }

    /**
     * Deserialize char by {@link #charToBytesWithFlag(char)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized char
     */
    public static char charFromBytesWithFlag(byte[] bytes) {
        byte flag = bytes[0];
        if (flag != FLAG_CHAR) {
            throw new ClassCastException(String.format("char cannot be deserialized in '%s' flag type", flag));
        }
        return (char) ((bytes[1] << 8) +
                (bytes[2] & 0xFF));
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