package com.ironz.binaryprefs.serialization.io;

import com.ironz.binaryprefs.serialization.Bits;
import com.ironz.binaryprefs.serialization.Persistable;

public final class BinaryPrefsObjectInputImpl implements DataInput {

    private int offset = 0;
    private byte[] buffer;

    @Override
    public <T extends Persistable> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        checkBytes(bytes);
        checkNull(clazz);
        checkPersistable(bytes);

        this.buffer = bytes;

        T instance = clazz.newInstance();
        instance.readExternal(this);

        return instance;
    }

    @Override
    public boolean readBoolean() {
        checkBounds();
        boolean b = Bits.booleanFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_BOOLEAN;
        return b;
    }

    @Override
    public byte readByte() {
        checkBounds();
        byte b = Bits.byteFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_BYTE;
        return b;
    }

    @Override
    public short readShort() {
        checkBounds();
        short s = Bits.shortFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_SHORT;
        return s;
    }

    @Override
    public char readChar() {
        checkBounds();
        char c = Bits.charFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_CHAR;
        return c;
    }

    @Override
    public int readInt() {
        checkBounds();
        int i = Bits.intFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_INT;
        return i;
    }

    @Override
    public long readLong() {
        checkBounds();
        long l = Bits.longFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_LONG;
        return l;
    }

    @Override
    public float readFloat() {
        checkBounds();
        float f = Bits.floatFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_FLOAT;
        return f;
    }

    @Override
    public double readDouble() {
        checkBounds();
        double d = Bits.doubleFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_DOUBLE;
        return d;
    }

    @Override
    public String readString() {
        checkBounds();
        int bytesStringSize = Bits.intFromBytesWithFlag(buffer, offset);
        offset += Persistable.SIZE_INT;
        String s = Bits.stringFromBytesWithFlag(buffer, offset, bytesStringSize);
        offset += Persistable.SIZE_STRING + bytesStringSize;
        return s;
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

    private void checkPersistable(byte[] bytes) {
        byte flag = bytes[0];
        if (flag != Persistable.FLAG_PERSISTABLE) {
            throw new ClassCastException(String.format("Persistable cannot be deserialized in '%s' flag type", flag));
        }
        offset++;
    }

    private void checkBytes(byte[] bytes) {
        if (bytes.length < 1) {
            throw new UnsupportedOperationException("Cannot deserialize empty byte array!");
        }
    }
}