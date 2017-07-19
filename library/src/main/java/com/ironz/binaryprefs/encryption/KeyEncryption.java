package com.ironz.binaryprefs.encryption;

/**
 * Contract which describe how key will be converted
 */
public interface KeyEncryption {

    /**
     * Decrypts name from encrypted to original
     *
     * @param name encrypted name
     * @return original name
     */
    String decrypt(String name);

    /**
     * Encrypts name from original to encrypted
     *
     * @param name original name
     * @return encrypted name
     */
    String encrypt(String name);

    /**
     * Return name as is.
     */
    KeyEncryption NO_OP = new KeyEncryption() {
        @Override
        public String decrypt(String name) {
            return name;
        }

        @Override
        public String encrypt(String name) {
            return name;
        }
    };
}
