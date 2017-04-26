package com.ironz.binaryprefs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts bytes to primitives and backwards
 */
@SuppressWarnings("ConstantConditions")
public class Bits {

    private static final byte FLAG_STRING = -2;
    private static final byte FLAG_INT = -3;
    private static final byte FLAG_LONG = -4;
    private static final byte FLAG_FLOAT = -5;
    private static final byte FLAG_BOOLEAN = -6;

    private Bits() {
    }

    public static byte[] stringSetToBytes(Set<String> set) {

        ByteArrayOutputStream bs = null;
        DataOutputStream out = null;
        try {
            bs = new ByteArrayOutputStream();
            out = new DataOutputStream(bs);
            for (String element : set) {
                out.writeUTF(element);
            }
            return bs.toByteArray();
        } catch (Exception ignored) {
        } finally {
            try {
                bs.close();
                out.close();
            } catch (Exception ignored) {
            }
        }
        return new byte[0];
    }

    public static Set<String> stringSetFromBytes(byte[] b) {

        Set<String> set = new HashSet<>();

        ByteArrayInputStream bs = null;
        DataInputStream in = null;

        try {
            bs = new ByteArrayInputStream(b);
            in = new DataInputStream(bs);
            while (in.available() > 0) {
                String element = in.readUTF();
                set.add(element);
            }
        } catch (Exception ignored) {
        } finally {
            try {
                bs.close();
                in.close();
            } catch (Exception ignored) {

            }
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