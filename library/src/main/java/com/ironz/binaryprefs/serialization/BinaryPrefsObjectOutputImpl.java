package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.util.Bits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;

public final class BinaryPrefsObjectOutputImpl implements ObjectOutput {

    //bytes for initial array size, buffer array are resizable to (buffer.length + GROW_ARRAY_CAPACITY) after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 128;

    int offset = 0;
    private byte[] buffer = new byte[GROW_ARRAY_CAPACITY];

    @Override
    public void writeObject(Object obj) throws IOException {
        if (!(obj instanceof Externalizable)) {
            throw new UnsupportedOperationException("Can't serialize object '%s' because it's not  ");
        }
        Externalizable externalizable = (Externalizable) obj;
        externalizable.writeExternal(this);
    }

    @Override
    public void write(int value) throws IOException {
        byte[] bytes = Bits.byteToBytesWithFlag((byte) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] value) throws IOException {
        byte[] bytes = Bits.byteArrayToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        byte[] bytes = Bits.booleanToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeByte(int value) throws IOException {
        byte[] bytes = Bits.byteToBytesWithFlag((byte) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeShort(int value) throws IOException {
        byte[] bytes = Bits.shortToBytesWithFlag((short) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeChar(int value) throws IOException {
        byte[] bytes = Bits.charToBytesWithFlag((char) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeInt(int value) throws IOException {
        byte[] bytes = Bits.intToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeLong(long value) throws IOException {
        byte[] bytes = Bits.longToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        byte[] bytes = Bits.floatToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        byte[] bytes = Bits.doubleToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBytes(String value) throws IOException {
        byte[] trim = value.getBytes();
        for (byte b : trim) {
            byte[] bytes = Bits.byteToBytesWithFlag(b);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeChars(String value) throws IOException {
        char[] trim = value.toCharArray();
        for (char c : trim) {
            byte[] bytes = Bits.charToBytesWithFlag(c);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeUTF(String value) throws IOException {
        byte[] bytes = value.getBytes("UTF-8");
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] value, int off, int len) throws IOException {
        if (offset <= buffer.length) {
            growArray();
        }
        offset += len;
    }

    private void growArray() {
        byte[] bytes = new byte[buffer.length + GROW_ARRAY_CAPACITY];
        System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        buffer = bytes;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
