package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.persistable.Persistable;
import com.ironz.binaryprefs.serialization.persistable.io.BinaryPrefsObjectInputImpl;
import com.ironz.binaryprefs.serialization.persistable.io.BinaryPrefsObjectOutputImpl;
import com.ironz.binaryprefs.serialization.persistable.io.DataInput;
import com.ironz.binaryprefs.serialization.persistable.io.DataOutput;

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
     * Uses for detecting byte array primitive type of {@link Short}
     */
    static final byte FLAG_SHORT = -9;

    /**
     * Uses for detecting byte array primitive type of {@link Character}
     */
    static final byte FLAG_CHAR = -10;

    private static final int INITIAL_INTEGER_LENGTH = 5;

    private Bits() {
        throw new UnsupportedOperationException("Please, do not instantiate this class!");
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
            byte[] stringBytes = s.getBytes();
            byte[] stringSizeBytes = intToBytesWithFlag(stringBytes.length);

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

            Set<String> set = new HashSet<>();

            int i = 1;

            while (i < bytes.length) {

                byte[] stringSizeBytes = new byte[INITIAL_INTEGER_LENGTH];
                System.arraycopy(bytes, i, stringSizeBytes, 0, stringSizeBytes.length);
                int stringSize = intFromBytesWithFlag(stringSizeBytes);

                byte[] stringBytes = new byte[stringSize];

                for (int k = 0; k < stringBytes.length; k++) {
                    int stringOffset = i + k + INITIAL_INTEGER_LENGTH;
                    stringBytes[k] = bytes[stringOffset];
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
        return stringFromBytesWithFlag(bytes, 0, bytes.length - 1);
    }

    /**
     * Deserialize byte by {@link #stringToBytesWithFlag(String)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @param length length of array to write
     * @return deserialized String
     */
    public static String stringFromBytesWithFlag(byte[] bytes, int offset, int length) {
        byte flag = bytes[offset];
        if (flag != FLAG_STRING) {
            throw new ClassCastException(String.format("String cannot be deserialized in '%s' flag type", flag));
        }
        int flagOffset = 1;
        return new String(bytes, offset + flagOffset, length);
    }

    /**
     * Serialize {@code int} into byte array with following scheme:
     * [{@link #FLAG_INT}] + [int_bytes].
     *
     * @param value target int to serialize.
     * @return specific byte array with scheme.
     */
    public static byte[] intToBytesWithFlag(int value) {
        int i = 0xff;
        return new byte[]{
                FLAG_INT,
                (byte) ((value >>> 24) & i),
                (byte) ((value >>> 16) & i),
                (byte) ((value >>> 8) & i),
                (byte) ((value) & i)
        };
    }

    /**
     * Deserialize byte by {@link #intToBytesWithFlag(int)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized int
     */
    public static int intFromBytesWithFlag(byte[] bytes) {
        return intFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #intToBytesWithFlag(int)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized int
     */
    public static int intFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_INT) {
            throw new ClassCastException(String.format("int cannot be deserialized in '%s' flag type", flag));
        }
        int i = 0xff;
        return ((bytes[4 + offset] & i)) +
                ((bytes[3 + offset] & i) << 8) +
                ((bytes[2 + offset] & i) << 16) +
                ((bytes[1 + offset]) << 24);
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
        return longFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #longToBytesWithFlag(long)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized long
     */
    public static long longFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_LONG) {
            throw new ClassCastException(String.format("long cannot be deserialized in '%s' flag type", flag));
        }
        long l = 0xffL;
        return ((bytes[8 + offset] & l)) +
                ((bytes[7 + offset] & l) << 8) +
                ((bytes[6 + offset] & l) << 16) +
                ((bytes[5 + offset] & l) << 24) +
                ((bytes[4 + offset] & l) << 32) +
                ((bytes[3 + offset] & l) << 40) +
                ((bytes[2 + offset] & l) << 48) +
                (((long) bytes[1 + offset]) << 56);
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
                (byte) (l)
        };
    }

    /**
     * Deserialize byte by {@link #doubleToBytesWithFlag(double)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized double
     */
    static double doubleFromBytesWithFlag(byte[] bytes) {
        return doubleFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #doubleToBytesWithFlag(double)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized double
     */
    public static double doubleFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_DOUBLE) {
            throw new ClassCastException(String.format("double cannot be deserialized in '%s' flag type", flag));
        }
        int i = 0xff;
        long value = ((bytes[8 + offset] & i)) +
                ((bytes[7 + offset] & i) << 8) +
                ((bytes[6 + offset] & i) << 16) +
                ((long) (bytes[5 + offset] & i) << 24) +
                ((long) (bytes[4 + offset] & i) << 32) +
                ((long) (bytes[3 + offset] & i) << 40) +
                ((long) (bytes[2 + offset] & i) << 48) +
                ((long) (bytes[1 + offset]) << 56);
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
     * Deserialize byte by {@link #floatToBytesWithFlag(float)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized float
     */
    public static float floatFromBytesWithFlag(byte[] bytes) {
        return floatFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #floatToBytesWithFlag(float)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized float
     */
    public static float floatFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_FLOAT) {
            throw new ClassCastException(String.format("float cannot be deserialized in '%s' flag type", flag));
        }
        int i = 0xFF;
        int value = ((bytes[offset + 4] & i)) +
                ((bytes[offset + 3] & i) << 8) +
                ((bytes[offset + 2] & i) << 16) +
                ((bytes[offset + 1]) << 24);

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
        return booleanFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #booleanToBytesWithFlag(boolean)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized boolean
     */
    public static boolean booleanFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_BOOLEAN) {
            throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", flag));
        }
        return bytes[1 + offset] != 0;
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
     * Deserialize byte by {@link #byteToBytesWithFlag(byte)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized byte
     */
    static byte byteFromBytesWithFlag(byte[] bytes) {
        return byteFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize byte by {@link #byteToBytesWithFlag(byte)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized byte
     */
    public static byte byteFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_BYTE) {
            throw new ClassCastException(String.format("byte cannot be deserialized in '%s' flag type", flag));
        }
        return bytes[1 + offset];
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
    static short shortFromBytesWithFlag(byte[] bytes) {
        return shortFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize short by {@link #shortToBytesWithFlag(short)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized short
     */
    public static short shortFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_SHORT) {
            throw new ClassCastException(String.format("short cannot be deserialized in '%s' flag type", flag));
        }
        int i = 0xff;
        return (short) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
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
    static char charFromBytesWithFlag(byte[] bytes) {
        return charFromBytesWithFlag(bytes, 0);
    }

    /**
     * Deserialize char by {@link #charToBytesWithFlag(char)} convention
     *
     * @param bytes  target byte array for deserialization
     * @param offset offset of bytes array
     * @return deserialized char
     */
    public static char charFromBytesWithFlag(byte[] bytes, int offset) {
        byte flag = bytes[offset];
        if (flag != FLAG_CHAR) {
            throw new ClassCastException(String.format("char cannot be deserialized in '%s' flag type", flag));
        }
        int i = 0xFF;
        return (char) ((bytes[1 + offset] << 8) +
                (bytes[2 + offset] & i));
    }

    /**
     * Serialize {@code {@link Persistable }} into byte array with following scheme:
     * [{@link Persistable#FLAG_PERSISTABLE}] + [persistable_bytes].
     * see {@link BinaryPrefsObjectOutputImpl} implementation for
     * understanding serialization contract of "persistable_bytes" part.
     *
     * @param value target {@link Persistable} to serialize.
     * @return specific byte array with scheme.
     */
    public static <T extends Persistable> byte[] persistableToBytes(T value) {
        DataOutput output = new BinaryPrefsObjectOutputImpl();
        return output.serialize(value);
    }

    /**
     * Deserialize {@link Persistable} by {@link #persistableToBytes(Persistable)} convention
     *
     * @param bytes target byte array for deserialization
     * @return deserialized {@link Persistable}
     */
    public static <T extends Persistable> T persistableFromBytes(byte[] bytes, Class<T> clazz) {
        DataInput input = new BinaryPrefsObjectInputImpl();
        return input.deserialize(bytes, clazz);
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
        throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect flag '%s'", flag));
    }

    @SuppressWarnings("unchecked")
    public static byte[] trySerializeByType(Object o) {
        if (isStringHashSet(o)) {
            return stringSetToBytesWithFlag((Set<String>) o);
        }
        if (o instanceof String) {
            return stringToBytesWithFlag((String) o);
        }
        if (o instanceof Integer) {
            return intToBytesWithFlag((Integer) o);
        }
        if (o instanceof Long) {
            return longToBytesWithFlag((Long) o);
        }
        if (o instanceof Float) {
            return floatToBytesWithFlag((Float) o);
        }
        if (o instanceof Boolean) {
            return booleanToBytesWithFlag((Boolean) o);
        }
        throw new UnsupportedClassVersionError(String.format("Type verification failed. Incorrect type '%s'", o.getClass().getName()));
    }

    private static boolean isStringHashSet(Object set) {
        try {
            //noinspection unused,unchecked
            Set<String> stringSet = (Set<String>) set;
            return true;
        } catch (Exception ignored) {

        }
        return false;
    }
}