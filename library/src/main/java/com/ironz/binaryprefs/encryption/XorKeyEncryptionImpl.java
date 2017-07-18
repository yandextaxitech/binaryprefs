package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

import java.util.Arrays;

public final class XorKeyEncryptionImpl implements KeyEncryption {

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
        int originalLength = original.length;
        byte[] xored = new byte[originalLength];
        for (int index = 0; index < originalLength; index++) {
            xored[index] = xorByte(original[index]);
        }
        return new String(xored);
    }

    private byte xorByte(final byte beforeXorItem) {
        byte afterXorItem = beforeXorItem;
        int xorLength = xor.length;
        for (int index = 0; index < xorLength; index++) {
            afterXorItem ^= xor[index];
        }
        return afterXorItem;
    }

    private void checkMirror() {
        if (isEven()) {
            int half = xor.length / 2;
            byte[] firstHalf = Arrays.copyOfRange(xor, 0, half);
            byte[] secondHalf = Arrays.copyOfRange(xor, half, xor.length);
            Arrays.sort(firstHalf);
            Arrays.sort(secondHalf);
            if (Arrays.equals(firstHalf, secondHalf)) {
                throw new EncryptionException("xor must be not mirrored");
            }
        }
    }

    private boolean isEven() {
        return xor.length % 2 == 0;
    }

    private void checkLength() {
        if (xor.length < KEY_LENGTH) {
            throw new EncryptionException("xor must be at least 16 bytes");
        }
    }
}
