package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

import java.util.Arrays;

/**
 * @author Sergey Boishtyan
 */

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
        byte[] beforeXor = name.getBytes();
        int beforeXorLength = beforeXor.length;
        int xorLength = xor.length;
        byte[] afterXor = new byte[beforeXorLength];
        for (int beforeXorIndex = 0; beforeXorIndex < beforeXorLength; beforeXorIndex++) {
            byte afterXorItem = beforeXor[beforeXorIndex];
            for (int xorIndex = 0; xorIndex < xorLength; xorIndex++) {
                afterXorItem ^= xor[xorIndex];
            }
            afterXor[beforeXorIndex] = afterXorItem;
        }

        return new String(afterXor);
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
