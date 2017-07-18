package com.ironz.binaryprefs.encryption;

import com.ironz.binaryprefs.exception.EncryptionException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class XorKeyEncryptionImplTest {

    private final XorKeyEncryptionImpl xorKeyEncryption = new XorKeyEncryptionImpl("LZN8KKF7KH816D0U".getBytes());

    @Test
    public void decrypt() {
        String origin = "origin";
        String decrypt = xorKeyEncryption.decrypt(origin);
        assertNotEquals(origin, decrypt);

        String encrypt = xorKeyEncryption.encrypt(decrypt);
        assertEquals(origin, encrypt);
    }

    @Test
    public void encrypt() {
        String origin = "origin";
        String encrypted = xorKeyEncryption.encrypt(origin);
        assertNotEquals(origin, encrypted);

        String decrypt = xorKeyEncryption.decrypt(encrypted);
        assertEquals(origin, decrypt);
    }

    @Test
    public void incorrectDecryptWithAnotherEncryption() {
        String origin = "origin";
        String encrypted = xorKeyEncryption.encrypt(origin);

        XorKeyEncryptionImpl anotherEncryption = new XorKeyEncryptionImpl("1111111111111110".getBytes());
        String decryptByAnother = anotherEncryption.decrypt(encrypted);

        assertNotEquals(origin, decryptByAnother);
    }

    @Test
    public void givenXorOddSizeThenNoEncryptionException() {
        String withOddSize = "11111111111111111";
        new XorKeyEncryptionImpl(withOddSize.getBytes());
    }

    @Test(expected = EncryptionException.class)
    public void givenXorEvenSizeAndMirroredThenEncryptionException() {
        String evenWithMirror = "0101010101010101";
        new XorKeyEncryptionImpl(evenWithMirror.getBytes());
    }

    @Test
    public void givenXorEvenSizeAndNotMirroredThenNoEncryptionException() {
        String evenWithMirror = "1111111111111110";
        new XorKeyEncryptionImpl(evenWithMirror.getBytes());
    }

    @Test(expected = EncryptionException.class)
    public void givenXorSizeLessThenSixteenThenEncryptionException() {
        String withSizeLessThenSixteen = "";
        new XorKeyEncryptionImpl(withSizeLessThenSixteen.getBytes());
    }

    @Test
    public void givenXorSizeGreaterThenFifthTeenThenNoEncryptionException() {
        String withSizeGreaterThenFifthTeen = "11111111111111111";
        new XorKeyEncryptionImpl(withSizeGreaterThenFifthTeen.getBytes());
    }
}