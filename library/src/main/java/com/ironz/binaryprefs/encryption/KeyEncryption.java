package com.ironz.binaryprefs.encryption;

public interface KeyEncryption {

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

    String decrypt(String name);

    String encrypt(String name);
}
