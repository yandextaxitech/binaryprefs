package com.ironz.binaryprefs.encryption;

import java.nio.charset.Charset;

/**
 * Custom implementation of Base32 but with lower cased bytes table
 */
public class SafeEncoder {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final byte[] ENCODE_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'
    };

    private static final int MASK_8_BITS = 0xff;
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MASK_5BITS = 0x1f;
    private static final int BITS_PER_ENCODED_BYTE = 5;
    private static final int BYTES_PER_ENCODED_BLOCK = 8;
    private static final int BYTES_PER_UN_ENCODED_BLOCK = 5;

    private byte[] buffer;
    private int position;

    private boolean eof;
    private int modulus;
    private long bitWorkArea;

    private byte decode(byte octet) {
        switch (octet) {
            case '0':
            case 'O':
            case 'o':
                return 0;

            case '1':
            case 'I':
            case 'i':
            case 'L':
            case 'l':
                return 1;

            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;

            case 'A':
            case 'a':
                return 10;

            case 'B':
            case 'b':
                return 11;

            case 'C':
            case 'c':
                return 12;

            case 'D':
            case 'd':
                return 13;

            case 'E':
            case 'e':
                return 14;

            case 'F':
            case 'f':
                return 15;

            case 'G':
            case 'g':
                return 16;

            case 'H':
            case 'h':
                return 17;

            case 'J':
            case 'j':
                return 18;

            case 'K':
            case 'k':
                return 19;

            case 'M':
            case 'm':
                return 20;

            case 'N':
            case 'n':
                return 21;

            case 'P':
            case 'p':
                return 22;

            case 'Q':
            case 'q':
                return 23;

            case 'R':
            case 'r':
                return 24;

            case 'S':
            case 's':
                return 25;

            case 'T':
            case 't':
                return 26;

            case 'U':
            case 'u':
            case 'V':
            case 'v':
                return 27;

            case 'W':
            case 'w':
                return 28;

            case 'X':
            case 'x':
                return 29;

            case 'Y':
            case 'y':
                return 30;

            case 'Z':
            case 'z':
                return 31;

            default:
                return -1;
        }
    }

    private boolean isInAlphabet(byte octet) {
        return decode(octet) != -1;
    }

    private int available() {
        return buffer != null ? position : 0;
    }

    private void resizeBuffer() {
        if (buffer == null) {
            buffer = new byte[DEFAULT_BUFFER_SIZE];
            position = 0;
        } else {
            byte[] bytes = new byte[buffer.length * DEFAULT_BUFFER_RESIZE_FACTOR];
            System.arraycopy(buffer, 0, bytes, 0, buffer.length);
            buffer = bytes;
        }
    }

    private void ensureBufferSize(int size) {
        if ((buffer == null) || (buffer.length < position + size)) {
            resizeBuffer();
        }
    }

    private void readResults(byte[] bytes) {
        if (buffer != null) {
            int len = available();
            System.arraycopy(buffer, 0, bytes, 0, len);
            buffer = null;
        }
    }

    private void reset() {
        buffer = null;
        position = 0;
        modulus = 0;
        eof = false;
    }

    public String encodeToString(byte[] bytes) {
        return new String(encode(bytes), CHARSET);
    }

    public byte[] decode(String s) {
        return decode(s.getBytes(CHARSET));
    }

    private byte[] encode(byte[] bytes) {
        reset();
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        encode(bytes, bytes.length);
        encode(bytes, -1);
        byte[] result = new byte[position];
        readResults(result);
        return result;
    }

    private byte[] decode(byte[] bytes) {
        reset();
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        decode(bytes, bytes.length);
        decode(bytes, -1);
        byte[] result = new byte[position];
        readResults(result);
        return result;
    }

    private void encode(byte[] in, int available) {
        int localPosition = 0;
        if (eof) {
            return;
        }
        int encodeSize = BYTES_PER_ENCODED_BLOCK;
        if (available < 0) {
            eof = true;
            if (0 == modulus) {
                return;
            }
            ensureBufferSize(encodeSize);
            switch (modulus) {
                case 1:
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 3) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea << 2) & MASK_5BITS];
                    break;

                case 2:
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 11) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 6) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 1) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea << 4) & MASK_5BITS];
                    break;
                case 3:
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 19) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 14) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 9) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 4) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea << 1) & MASK_5BITS];
                    break;
                case 4:
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 27) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 22) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 17) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 12) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 7) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 2) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea << 3) & MASK_5BITS];
                    break;
            }
        } else {
            for (int i = 0; i < available; i++) {
                ensureBufferSize(encodeSize);
                modulus = (modulus + 1) % BYTES_PER_UN_ENCODED_BLOCK;
                int b = in[localPosition++];
                if (b < 0) {
                    b += 256;
                }
                bitWorkArea = (bitWorkArea << 8) + b;
                if (0 == modulus) {
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 35) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 30) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 25) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 20) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 15) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 10) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) (bitWorkArea >> 5) & MASK_5BITS];
                    buffer[position++] = ENCODE_TABLE[(int) bitWorkArea & MASK_5BITS];
                }
            }
        }
    }

    private void decode(byte[] in, int available) {
        int localPosition = 0;
        if (eof) {
            return;
        }
        if (available < 0) {
            eof = true;
        }
        int decodeSize = BYTES_PER_ENCODED_BLOCK - 1;
        for (int i = 0; i < available; i++) {
            byte b = in[localPosition++];
            ensureBufferSize(decodeSize);
            if (isInAlphabet(b)) {
                int result = decode(b);
                modulus = (modulus + 1) % BYTES_PER_ENCODED_BLOCK;
                bitWorkArea = (bitWorkArea << BITS_PER_ENCODED_BYTE) + result;
                if (modulus == 0) {
                    buffer[position++] = (byte) ((bitWorkArea >> 32) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 24) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 16) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 8) & MASK_8_BITS);
                    buffer[position++] = (byte) (bitWorkArea & MASK_8_BITS);
                }
            }
        }

        if (eof && modulus >= 2) {
            ensureBufferSize(decodeSize);

            switch (modulus) {
                case 2:
                    buffer[position++] = (byte) ((bitWorkArea >> 2) & MASK_8_BITS);
                    break;
                case 3:
                    buffer[position++] = (byte) ((bitWorkArea >> 7) & MASK_8_BITS);
                    break;
                case 4:
                    bitWorkArea = bitWorkArea >> 4;
                    buffer[position++] = (byte) ((bitWorkArea >> 8) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea) & MASK_8_BITS);
                    break;
                case 5:
                    bitWorkArea = bitWorkArea >> 1;
                    buffer[position++] = (byte) ((bitWorkArea >> 16) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 8) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea) & MASK_8_BITS);
                    break;
                case 6:
                    bitWorkArea = bitWorkArea >> 6;
                    buffer[position++] = (byte) ((bitWorkArea >> 16) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 8) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea) & MASK_8_BITS);
                    break;
                case 7:
                    bitWorkArea = bitWorkArea >> 3;
                    buffer[position++] = (byte) ((bitWorkArea >> 24) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 16) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea >> 8) & MASK_8_BITS);
                    buffer[position++] = (byte) ((bitWorkArea) & MASK_8_BITS);
                    break;
            }
        }
    }
}