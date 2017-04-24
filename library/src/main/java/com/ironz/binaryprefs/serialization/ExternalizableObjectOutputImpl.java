package com.ironz.binaryprefs.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;

public final class ExternalizableObjectOutputImpl implements ObjectOutput {

    //32 bits for initial array size, buffer array are resizable to (buffer.length + GROW_ARRAY_CAPACITY) after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 32;

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
        offset++;
    }

    @Override
    public void write(byte[] b) throws IOException {

    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {

    }

    @Override
    public void writeBoolean(boolean v) throws IOException {

    }

    @Override
    public void writeByte(int v) throws IOException {

    }

    @Override
    public void writeShort(int v) throws IOException {

    }

    @Override
    public void writeChar(int v) throws IOException {

    }

    @Override
    public void writeInt(int v) throws IOException {

    }

    @Override
    public void writeLong(long v) throws IOException {

    }

    @Override
    public void writeFloat(float v) throws IOException {

    }

    @Override
    public void writeDouble(double v) throws IOException {

    }

    @Override
    public void writeBytes(String s) throws IOException {

    }

    @Override
    public void writeChars(String s) throws IOException {

    }

    @Override
    public void writeUTF(String s) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
