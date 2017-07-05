package com.ironz.binaryprefs.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implementation of {@link ByteEncryption} class which uses AES for <code>byte[]</code> data encryption.
 */
public final class AesByteEncryptionImpl implements ByteEncryption {

    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5PADDING";

    private final byte[] secretKeyBytes;
    private final byte[] initialVector;

    @SuppressWarnings("WeakerAccess")
    public AesByteEncryptionImpl(byte[] secretKeyBytes, byte[] initialVector) {
        this.secretKeyBytes = secretKeyBytes;
        this.initialVector = initialVector;
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        synchronized (AesByteEncryptionImpl.class) {
            return encryptInternal(bytes);
        }
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        synchronized (AesByteEncryptionImpl.class) {
            return decryptInternal(bytes);
        }
    }

    private byte[] encryptInternal(byte[] bytes) {
        try {
            return createEncryptCipher().doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decryptInternal(byte[] bytes) {
        try {
            return createDecryptCipher().doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher createEncryptCipher() {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, AES);
            IvParameterSpec iv = new IvParameterSpec(initialVector);
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher createDecryptCipher() {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, AES);
            IvParameterSpec iv = new IvParameterSpec(initialVector);
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}