package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;
import org.junit.Test;

import static com.ironz.binaryprefs.impl.UnicodeCharacters.UTF_CHARACTERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public final class XorKeyEncryptionTest {

    private final KeyEncryption encryption = new XorKeyEncryption("LZN8KKF7KH816D0U".getBytes());
    private final KeyEncryption badEncryption = new XorKeyEncryption("1111111111111110".getBytes());

    @Test
    public void encryptDecrypt() {

        String encrypt = encryption.encrypt(UTF_CHARACTERS);
        String decrypt = encryption.decrypt(encrypt);

        assertNotEquals(UTF_CHARACTERS, encrypt);
        assertEquals(UTF_CHARACTERS, decrypt);
    }

    @Test
    public void anotherEncryption() {
        String encrypted = encryption.encrypt(UTF_CHARACTERS);

        String decryptByAnother = badEncryption.decrypt(encrypted);

        assertNotEquals(UTF_CHARACTERS, decryptByAnother);
    }

    @Test
    public void oddSize() {
        new XorKeyEncryption("11111111111111111".getBytes());
    }

    @Test(expected = EncryptionException.class)
    public void evenAndMirrored() {
        new XorKeyEncryption("0101010101010101".getBytes());
    }

    @Test
    public void evenNotMirrored() {
        new XorKeyEncryption("1111111111111110".getBytes());
    }

    @Test(expected = EncryptionException.class)
    public void incorrectKeySize() {
        new XorKeyEncryption("".getBytes());
    }
}