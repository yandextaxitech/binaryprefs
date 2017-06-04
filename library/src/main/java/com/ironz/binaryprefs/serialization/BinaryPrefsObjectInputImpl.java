package com.ironz.binaryprefs.serialization;

import java.io.IOException;
import java.io.ObjectInput;

public final class BinaryPrefsObjectInputImpl implements ObjectInput {

    private final byte[] buffer;
    private int offset = 0;

    public BinaryPrefsObjectInputImpl(byte[] buffer) {
        this.buffer = buffer;
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {

        checkBounds();
        checkExternalizable();
        String className = getExternalizableClassName();

        return null;
    }

    private void checkBounds() {
        if (offset >= buffer.length - 1) {
            throw new ArrayIndexOutOfBoundsException("Can't read out of bounds array");
        }
    }

    private void checkExternalizable() {
        byte flag = buffer[offset];
        if (flag != Bits.FLAG_EXTERNALIZABLE) {
            throw new ClassCastException(String.format("Externalizable cannot be deserialized in '%s' flag type", flag));
        }
        offset++;
    }

    private String getExternalizableClassName() {
        return null;
    }

    @Override
    public int read() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public long skip(long n) throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public int available() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void readFully(byte[] b) throws IOException {

    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {

    }

    @Override
    public int skipBytes(int n) throws IOException {
        return 0;
    }

    @Override
    public boolean readBoolean() throws IOException {
        checkBounds();
        return false;
    }

    @Override
    public byte readByte() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public short readShort() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public char readChar() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public int readInt() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public long readLong() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public float readFloat() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        checkBounds();
        return 0;
    }

    @Override
    public String readLine() throws IOException {
        checkBounds();
        return null;
    }

    @Override
    public String readUTF() throws IOException {
        checkBounds();
        return null;
    }
}
