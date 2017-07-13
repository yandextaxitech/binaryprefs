package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

public final class XorByteEncryptionImpl implements ByteEncryption {

    private final byte[] secretKeyBytes;
    private final byte[] salt;

    private static final int KEY_LENGTH = 16;

    public XorByteEncryptionImpl(byte[] secretKeyBytes, byte[] salt) {
        this.secretKeyBytes = secretKeyBytes;
        this.salt = salt;
        checkLength();
    }

    private void checkLength() {
        if (secretKeyBytes.length < KEY_LENGTH || salt.length < KEY_LENGTH) {
            throw new EncryptionException("Secret and initial vector must be 16 bytes");
        }
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        return xor(bytes, secretKeyBytes, salt);
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        return xor(bytes, secretKeyBytes, salt);
    }

    private byte[] xor(final byte[] bytes, final byte[] secret, byte[] salt) {
        final byte[] output = new byte[bytes.length];
        int secretIndex = 0;
        int saltIndex = 0;
        for (int pos = 0; pos < bytes.length; ++pos) {
            output[pos] = (byte) (bytes[pos] ^ secret[secretIndex] ^ salt[saltIndex]);
            ++secretIndex;
            ++saltIndex;
            if (secretIndex >= secret.length) {
                secretIndex = 0;
            }
            if (saltIndex >= salt.length) {
                saltIndex = 0;
            }
        }
        return output;
    }
}