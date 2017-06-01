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
    public void write(int b) throws IOException {
        byte[] bytes = Bits.byteToBytesWithFlag((byte) b);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        byte[] bytes = Bits.booleanToBytesWithFlag(v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeByte(int v) throws IOException {
        byte[] bytes = Bits.byteToBytesWithFlag((byte) v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeShort(int v) throws IOException {
        byte[] bytes = Bits.shortToBytesWithFlag((short) v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeChar(int v) throws IOException {
        byte[] bytes = Bits.charToBytesWithFlag((char) v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeInt(int v) throws IOException {
        byte[] bytes = Bits.intToBytesWithFlag(v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeLong(long v) throws IOException {
        byte[] bytes = Bits.longToBytesWithFlag(v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        byte[] bytes = Bits.floatToBytesWithFlag(v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        byte[] bytes = Bits.doubleToBytesWithFlag(v);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        byte[] trim = s.getBytes();
        for (byte b : trim) {
            byte[] bytes = Bits.byteToBytesWithFlag(b);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeChars(String s) throws IOException {
        char[] trim = s.toCharArray();
        for (char c : trim) {
            byte[] bytes = Bits.charToBytesWithFlag(c);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes("UTF-8");
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (offset <= buffer.length) {
            drainArray();
        }
        offset += len;
    }

    private void drainArray() {
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
