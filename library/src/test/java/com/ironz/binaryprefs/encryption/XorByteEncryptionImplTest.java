package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class XorByteEncryptionImplTest {

    private static final byte[] SECRET_KEY_BYTES = "LZN8KKF7KH816D0U".getBytes();
    private static final byte[] SALT_BYTES = "AG6PJGG7AZJD8QPH".getBytes();
    private static final byte[] BAD_SECRET_KEY_BYTES = "0000000000000000".getBytes();
    private static final byte[] BAD_SALT = "0000000000000000".getBytes();

    private ByteEncryption byteEncryption;
    private ByteEncryption badByteEncryption;

    @Before
    public void setUp() {
        byteEncryption = new XorByteEncryptionImpl(SECRET_KEY_BYTES, SALT_BYTES);
        badByteEncryption = new XorByteEncryptionImpl(BAD_SECRET_KEY_BYTES, BAD_SALT);
    }

    @Test
    public void encryptDecrypt() {
        String original = "some string";
        byte[] originalBytes = original.getBytes();

        byte[] encrypt = byteEncryption.encrypt(originalBytes);
        byte[] decrypt = byteEncryption.decrypt(encrypt);
        String encryptedString = new String(encrypt);
        String restored = new String(decrypt);

        assertNotEquals(original, encryptedString);
        assertEquals(original, restored);
    }

    @Test
    public void badDecrypt() {
        String original = "some string";
        byte[] originalBytes = original.getBytes();

        byte[] encrypt = byteEncryption.encrypt(originalBytes);

        byte[] decrypt = badByteEncryption.decrypt(encrypt);
        String result = new String(decrypt);

        assertNotEquals(original, result);
    }

    @Test(expected = EncryptionException.class)
    public void incorrectKeySize() {
        new XorByteEncryptionImpl(new byte[0], new byte[0]);
    }
}