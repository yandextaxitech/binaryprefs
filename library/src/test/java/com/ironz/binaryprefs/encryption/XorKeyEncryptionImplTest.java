package com.ironz.binaryprefs.encryption;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class XorKeyEncryptionImplTest {

    private final XorKeyEncryptionImpl xorKeyEncryption = new XorKeyEncryptionImpl("111111111111111011".getBytes());

    @Test
    public void decrypt() throws Exception {
        String origin = "origin";
        String decrypt = xorKeyEncryption.decrypt(origin);
        assertNotEquals(origin, decrypt);

        String encrypt = xorKeyEncryption.encrypt(decrypt);
        assertEquals(origin, encrypt);
    }

    @Test
    public void encrypt() throws Exception {
        String origin = "origin";
        String encrypted = xorKeyEncryption.encrypt(origin);
        assertNotEquals(origin, encrypted);

        String decrypt = xorKeyEncryption.decrypt(encrypted);
        assertEquals(origin, decrypt);
    }

}