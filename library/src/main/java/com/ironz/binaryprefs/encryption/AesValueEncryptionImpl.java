package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implementation of {@link ValueEncryption} class which uses AES for <code>byte[]</code> data encryption.
 */
public final class AesValueEncryptionImpl implements ValueEncryption {

    private static final String SHORT_KEYS_MESSAGE = "Secret and initial vector must be 16 bytes";

    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5PADDING";
    private static final int KEY_LENGTH = 16;

    private final Cipher cipher;
    private final SecretKeySpec keySpec;
    private final IvParameterSpec iv;

    /**
     * Creates byte encryption instance which performs AES vice versa encryption operation.
     *
     * @param secretKeyBytes 16 bytes secret key
     * @param initialVector  16 bytes initial vector
     */
    public AesValueEncryptionImpl(byte[] secretKeyBytes, byte[] initialVector) {
        checkLength(secretKeyBytes, initialVector);
        this.cipher = getCipherInstance();
        this.keySpec = new SecretKeySpec(secretKeyBytes, AES);
        this.iv = new IvParameterSpec(initialVector);
    }

    private void checkLength(byte[] secretKeyBytes, byte[] initialVector) {
        if (secretKeyBytes.length != KEY_LENGTH || initialVector.length != KEY_LENGTH) {
            throw new EncryptionException(SHORT_KEYS_MESSAGE);
        }
    }

    private Cipher getCipherInstance() {
        try {
            return Cipher.getInstance(AES_CBC_PKCS5_PADDING);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        return encryptInternal(bytes);
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        return decryptInternal(bytes);
    }

    private byte[] encryptInternal(byte[] bytes) {
        try {
            Cipher encryptCipher = createEncryptCipher();
            return encryptCipher.doFinal(bytes);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    private byte[] decryptInternal(byte[] bytes) {
        try {
            Cipher decryptCipher = createDecryptCipher();
            return decryptCipher.doFinal(bytes);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    private Cipher createEncryptCipher() throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        return cipher;
    }

    private Cipher createDecryptCipher() throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        return cipher;
    }
}