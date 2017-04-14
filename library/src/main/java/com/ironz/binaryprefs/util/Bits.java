package com.ironz.binaryprefs.util;

/**
 * Converts bytes to primitives and backwards
 */
public class Bits {

    private Bits() {
    }

    public static int intFromBytes(byte[] b) {
        int i = 0xFF;
        return ((b[3] & i)) +
                ((b[2] & i) << 8) +
                ((b[1] & i) << 16) +
                ((b[0]) << 24);
    }

    public static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    public static float floatFromBytes(byte[] bytes) {
        int i = Bits.intFromBytes(bytes);
        return Float.intBitsToFloat(i);
    }

    public static byte[] floatToBytes(float value) {
        int i = Float.floatToIntBits(value);
        return intToBytes(i);
    }

    public static long longFromBytes(byte[] b) {
        long l = 0xFFL;
        return ((b[7] & l)) +
                ((b[6] & l) << 8) +
                ((b[5] & l) << 16) +
                ((b[4] & l) << 24) +
                ((b[3] & l) << 32) +
                ((b[2] & l) << 40) +
                ((b[1] & l) << 48) +
                (((long) b[0]) << 56);
    }

    public static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (value);
        bytes[6] = (byte) (value >>> 8);
        bytes[5] = (byte) (value >>> 16);
        bytes[4] = (byte) (value >>> 24);
        bytes[3] = (byte) (value >>> 32);
        bytes[2] = (byte) (value >>> 40);
        bytes[1] = (byte) (value >>> 48);
        bytes[0] = (byte) (value >>> 56);
        return bytes;
    }

    public static boolean booleanFromBytes(byte[] bytes) {
        return bytes[0] != 0;
    }

    public static byte[] booleanToBytes(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }
}
