package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AesByteEncryptionImplTest {

    private AesByteEncryptionImpl aesByteEncryption;
    private AesByteEncryptionImpl badAesByteEncryption;

    @Before
    public void setUp() {
        aesByteEncryption = new AesByteEncryptionImpl("LZN8KKF7KH816D0U".getBytes(), "AG6PJGG7AZJD8QPH".getBytes());
        badAesByteEncryption = new AesByteEncryptionImpl("0000000000000000".getBytes(), "0000000000000000".getBytes());
    }

    @Test
    public void encryptDecrypt() {
        String original = "some string";
        byte[] encrypt = aesByteEncryption.encrypt(original.getBytes());
        byte[] decrypt = aesByteEncryption.decrypt(encrypt);
        String restored = new String(decrypt);
        assertEquals(original, restored);
    }

    @Test(expected = EncryptionException.class)
    public void badDecrypt() {
        String original = "some string";
        byte[] encrypt = aesByteEncryption.encrypt(original.getBytes());
        badAesByteEncryption.decrypt(encrypt);
    }

    @Test(expected = EncryptionException.class)
    public void incorrectKeySize() {
        new AesByteEncryptionImpl(new byte[0], new byte[0]);
    }
}