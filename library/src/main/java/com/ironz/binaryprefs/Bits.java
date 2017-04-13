package com.ironz.binaryprefs;

class Bits {

    static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    static byte[] floatToBytes(float value) {
        int i = Float.floatToIntBits(value);
        return intToBytes(i);
    }

    static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    static byte[] booleanToBytes(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }

    static int intFromBytes(byte[] b) {
        int i = 0xFF;
        return ((b[3] & i)) +
                ((b[2] & i) << 8) +
                ((b[1] & i) << 16) +
                ((b[0]) << 24);
    }

    static long longFromBytes(byte[] b) {
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

    static boolean booleanFromBytes(byte[] bytes) {
        return bytes[0] != 0;
    }
}
