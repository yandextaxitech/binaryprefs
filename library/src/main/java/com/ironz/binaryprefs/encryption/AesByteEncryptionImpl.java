package com.ironz.binaryprefs.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesByteEncryptionImpl implements ByteEncryption {

    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5PADDING";

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    @SuppressWarnings("WeakerAccess")
    public AesByteEncryptionImpl(byte[] secretKeyBytes, byte[] initialVector) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, AES);
            IvParameterSpec iv = new IvParameterSpec(initialVector);
            encryptCipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            decryptCipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        try {
            return encryptCipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        try {
            return decryptCipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}