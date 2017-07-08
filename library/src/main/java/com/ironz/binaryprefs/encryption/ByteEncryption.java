package com.ironz.binaryprefs.encryption;

/**
 * Contract which describe how byte array will be converted
 */
public interface ByteEncryption {
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
    @SuppressWarnings("unused")
    ByteEncryption NO_OP = new ByteEncryption() {
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