package com.ironz.binaryprefs.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;

public final class BinaryPrefsObjectOutputImpl implements ObjectOutput {

    //bytes for initial array size, buffer array are resizable to (buffer.length + GROW_ARRAY_CAPACITY) after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 128;

    int offset = 0;
    private byte[] buffer = new byte[GROW_ARRAY_CAPACITY];
    boolean closed = false;

    @Override
    public void writeObject(Object value) throws IOException {
        checkNull(value);
        checkExternalizable(value);
        checkClosed();

        byte[] bytes = {Bits.FLAG_EXTERNALIZABLE};
        write(bytes, 0, bytes.length);
        String name = value.getClass().getName();
        writeUTF(name);
        Externalizable externalizable = (Externalizable) value;
        externalizable.writeExternal(this);
    }

    @Override
    public void write(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.byteToBytesWithFlag((byte) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.byteArrayToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.booleanToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeByte(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.byteToBytesWithFlag((byte) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeShort(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.shortToBytesWithFlag((short) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeChar(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.charToBytesWithFlag((char) value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeInt(int value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.intToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeLong(long value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.longToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.floatToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        checkClosed();
        byte[] bytes = Bits.doubleToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBytes(String value) throws IOException {
        checkClosed();
        checkNull(value);
        byte[] trim = value.getBytes();
        for (byte b : trim) {
            byte[] bytes = Bits.byteToBytesWithFlag(b);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeChars(String value) throws IOException {
        checkClosed();
        checkNull(value);
        char[] trim = value.toCharArray();
        for (char c : trim) {
            byte[] bytes = Bits.charToBytesWithFlag(c);
            write(bytes, 0, bytes.length);
        }
    }

    @Override
    public void writeUTF(String value) throws IOException {
        checkClosed();
        checkNull(value);
        byte[] bytes = Bits.stringToBytesWithFlag(value);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] value, int off, int len) throws IOException {
        checkClosed();
        checkBounds(value, off, len);
        if (offset + len <= buffer.length) {
            growArray(len);
        }
        System.arraycopy(value, off, buffer, offset, len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    private void growArray(int len) {
        byte[] bytes = new byte[buffer.length + GROW_ARRAY_CAPACITY];
        System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        buffer = bytes;
        offset += len;
    }

    private void checkNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null object");
        }
    }

    private void checkExternalizable(Object value) {
        if (!(value instanceof Externalizable)) {
            throw new UnsupportedOperationException(
                    String.format("Can't serialize object '%s' which are not implements '%s' interface",
                            value.getClass().getSimpleName(),
                            Externalizable.class.getName())
            );
        }
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Cannot write to already closed object output");
        }
    }

    private void checkBounds(byte[] value, int off, int len) {
        if (off > value.length || len > value.length || (value.length - off) < len) {
            throw new ArrayIndexOutOfBoundsException("Can't write out of bounds array");
        }
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[offset];
        System.arraycopy(buffer, 0, bytes, 0, offset);
        return bytes;
    }
}