package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

import java.util.Arrays;

public final class XorKeyEncryptionImpl implements KeyEncryption {

    private static final String SMALL_XOR_MESSAGE = "XOR must be at least 16 bytes";
    private static final String MIRRORED_XOR_MESSAGE = "XOR must not be mirrored";
    private static final int KEY_LENGTH = 16;

    private final byte[] xor;

    public XorKeyEncryptionImpl(byte[] xor) {
        this.xor = xor;
        checkLength();
        checkMirror();
    }

    @Override
    public String decrypt(String name) {
        return xorName(name);
    }

    @Override
    public String encrypt(String name) {
        return xorName(name);
    }

    private String xorName(String name) {
        byte[] original = name.getBytes();
        int length = original.length;
        byte[] result = new byte[length];
        for (int index = 0; index < length; index++) {
            byte b = original[index];
            result[index] = xorByte(b);
        }
        return new String(result);
    }

    private byte xorByte(byte raw) {
        byte temp = raw;
        for (byte b : xor) {
            temp ^= b;
        }
        return temp;
    }

    private void checkMirror() {
        if (!isEven()) {
            return;
        }
        int half = xor.length / 2;
        byte[] firstHalf = Arrays.copyOfRange(xor, 0, half);
        byte[] secondHalf = Arrays.copyOfRange(xor, half, xor.length);
        Arrays.sort(firstHalf);
        Arrays.sort(secondHalf);
        if (Arrays.equals(firstHalf, secondHalf)) {
            throw new EncryptionException(MIRRORED_XOR_MESSAGE);
        }
    }

    private boolean isEven() {
        return xor.length % 2 == 0;
    }

    private void checkLength() {
        if (xor.length < KEY_LENGTH) {
            throw new EncryptionException(SMALL_XOR_MESSAGE);
        }
    }
}