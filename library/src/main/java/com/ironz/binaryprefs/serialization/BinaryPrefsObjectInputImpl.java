package com.ironz.binaryprefs.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;

public final class BinaryPrefsObjectInputImpl implements ObjectInput {

    private byte[] buffer;
    private int offset = 0;
    private boolean closed = false;

    public <T extends Externalizable> T deserialize(byte[] bytes, Class<? extends T> clazz) throws Exception {

        checkNull(clazz);
        checkClosed();

        T instance = clazz.newInstance();
        instance.readExternal(this);
        return instance;
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        throw new UnsupportedOperationException("This deserialization type does not supported!");
    }

    @Override
    public int read() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public long skip(long n) throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public int available() throws IOException {
        checkClosed();

        checkBounds();
        return 0;
    }

    @Override
    public void close() throws IOException {
        closed = true;
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
        checkClosed();
        checkBounds();
        return false;
    }

    @Override
    public byte readByte() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public short readShort() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public char readChar() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public int readInt() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public long readLong() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public float readFloat() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        checkClosed();
        checkBounds();
        return 0;
    }

    @Override
    public String readLine() throws IOException {
        checkClosed();
        checkBounds();
        return null;
    }

    @Override
    public String readUTF() throws IOException {
        checkClosed();
        checkBounds();
        return null;
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Cannot write to already closed object output");
        }
    }

    private void checkBounds() {
        if (offset >= buffer.length - 1) {
            throw new ArrayIndexOutOfBoundsException("Can't read out of bounds array");
        }
    }

    private void checkNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null object");
        }
    }
}