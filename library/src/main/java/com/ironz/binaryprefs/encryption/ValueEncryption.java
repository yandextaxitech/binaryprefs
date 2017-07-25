package com.ironz.binaryprefs.encryption;

/**
 * Contract which describe how value will be converted
 */
public interface ValueEncryption {
    /**
     * Encrypts bytes from original to encrypted
     *
     * @param bytes original bytes
     * @return encrypted bytes
     */
    byte[] encrypt(byte[] bytes);

    /**
     * Decrypts bytes from encrypted to original
     *
     * @param bytes decrypted bytes
     * @return original bytes
     */
    byte[] decrypt(byte[] bytes);

    /**
     * Returns byte arrays as is.
     */
    ValueEncryption NO_OP = new ValueEncryption() {
        @Override
        public byte[] encrypt(byte[] bytes) {
            return bytes;
        }

        @Override
        public byte[] decrypt(byte[] bytes) {
            return bytes;
        }
    };
}