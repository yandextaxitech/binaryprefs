package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AesByteEncryptionImplTest {

    private static final byte[] SECRET_KEY_BYTES = "LZN8KKF7KH816D0U".getBytes();
    private static final byte[] INITIAL_VECTOR = "AG6PJGG7AZJD8QPH".getBytes();
    private static final byte[] BAD_SECRET_KEY_BYTES = "0000000000000000".getBytes();
    private static final byte[] BAD_INITIAL_VECTOR = "0000000000000000".getBytes();

    private ByteEncryption byteEncryption;
    private ByteEncryption badByteEncryption;

    @Before
    public void setUp() {
        byteEncryption = new AesByteEncryptionImpl(SECRET_KEY_BYTES, INITIAL_VECTOR);
        badByteEncryption = new AesByteEncryptionImpl(BAD_SECRET_KEY_BYTES, BAD_INITIAL_VECTOR);
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

    @Test(expected = EncryptionException.class)
    public void badDecrypt() {
        String original = "some string";
        byte[] originalBytes = original.getBytes();

        byte[] encrypt = byteEncryption.encrypt(originalBytes);

        badByteEncryption.decrypt(encrypt);
    }

    @Test(expected = EncryptionException.class)
    public void incorrectKeySize() {
        new AesByteEncryptionImpl(new byte[0], new byte[0]);
    }
}