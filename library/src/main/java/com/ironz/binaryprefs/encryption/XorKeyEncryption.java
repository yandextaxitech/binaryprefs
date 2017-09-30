package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

import java.util.Arrays;

public final class XorKeyEncryption implements KeyEncryption {

    private static final String SMALL_XOR_MESSAGE = "XOR must be at least 16 bytes";
    private static final String MIRRORED_XOR_MESSAGE = "XOR must not be mirrored";

    private static final int KEY_LENGTH = 16;

    private final byte[] xor;
    private final SafeEncoder safeEncoder;

    public XorKeyEncryption(byte[] xor) {
        this.xor = xor;
        this.safeEncoder = new SafeEncoder();
        checkLength();
        checkMirror();
    }

    private void checkLength() {
        if (xor.length < KEY_LENGTH) {
            throw new EncryptionException(SMALL_XOR_MESSAGE);
        }
    }

    private void checkMirror() {
        if (!isEven()) {
            return;
        }
        int halfSize = xor.length / 2;
        byte[] firstHalf = Arrays.copyOfRange(xor, 0, halfSize);
        byte[] secondHalf = Arrays.copyOfRange(xor, halfSize, xor.length);
        Arrays.sort(firstHalf);
        Arrays.sort(secondHalf);
        if (Arrays.equals(firstHalf, secondHalf)) {
            throw new EncryptionException(MIRRORED_XOR_MESSAGE);
        }
    }

    private boolean isEven() {
        return xor.length % 2 == 0;
    }

    @Override
    public String encrypt(String name) {
        byte[] original = name.getBytes();
        byte[] bytes = xorName(original);
        return safeEncoder.encodeToString(bytes);
    }

    @Override
    public String decrypt(String name) {
        byte[] decode = safeEncoder.decode(name);
        byte[] bytes = xorName(decode);
        return new String(bytes);
    }

    private byte[] xorName(byte[] original) {
        int length = original.length;
        byte[] result = new byte[length];
        for (int index = 0; index < length; index++) {
            byte b = original[index];
            result[index] = xorByte(b);
        }
        return result;
    }

    private byte xorByte(byte raw) {
        byte temp = raw;
        for (byte b : xor) {
            temp ^= b;
        }
        return temp;
    }
}