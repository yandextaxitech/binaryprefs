package com.ironz.binaryprefs.encryption;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AesByteEncryptionImplTest {

    private AesByteEncryptionImpl aesByteEncryption;

    @Before
    public void setUp() {
        aesByteEncryption = new AesByteEncryptionImpl("lzN8kkf7kH816D0U".getBytes(), "ag6pJqG7azJd8qpH".getBytes());
    }

    @Test
    public void encryptDecrypt() {
        String original = "some string";
        byte[] encrypt = aesByteEncryption.encrypt(original.getBytes());
        byte[] decrypt = aesByteEncryption.decrypt(encrypt);
        String restored = new String(decrypt);
        assertEquals(original, restored);
    }
}