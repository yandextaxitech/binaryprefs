package com.ironz.binaryprefs.serialization.io;

import com.ironz.binaryprefs.serialization.Bits;
import com.ironz.binaryprefs.serialization.Persistable;

public final class BinaryPrefsObjectOutputImpl implements DataOutput {

    //bytes for initial array size, buffer array are resizable to (buffer.length + GROW_ARRAY_CAPACITY) after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 18;


    private int offset = 0;
    private byte[] buffer = new byte[GROW_ARRAY_CAPACITY];

    @Override
    public <T extends Persistable> byte[] serialize(T value) {

        checkNull(value);

        write(new byte[]{Persistable.FLAG_PERSISTABLE});

        value.writeExternal(this);

        byte[] bytes = new byte[offset];
        System.arraycopy(buffer, 0, bytes, 0, offset);
        return bytes;
    }

    @Override
    public void writeBoolean(boolean value) {
        write(Bits.booleanToBytesWithFlag(value));
    }

    @Override
    public void writeByte(int value) {
        write(Bits.byteToBytesWithFlag((byte) value));
    }

    @Override
    public void writeShort(int value) {
        write(Bits.shortToBytesWithFlag((short) value));
    }

    @Override
    public void writeChar(int value) {
        write(Bits.charToBytesWithFlag((char) value));
    }

    @Override
    public void writeInt(int value) {
        write(Bits.intToBytesWithFlag(value));
    }

    @Override
    public void writeLong(long value) {
        write(Bits.longToBytesWithFlag(value));
    }

    @Override
    public void writeFloat(float value) {
        write(Bits.floatToBytesWithFlag(value));
    }

    @Override
    public void writeDouble(double value) {
        write(Bits.doubleToBytesWithFlag(value));
    }

    @Override
    public void writeString(String s) {
        write(Bits.intToBytesWithFlag(s.getBytes().length));
        write(Bits.stringToBytesWithFlag(s));
    }

    private void write(byte[] value) {
        int length = value.length;
        tryGrowArray(length);
        System.arraycopy(value, 0, buffer, offset, length);
        offset += length;
    }

    private void checkNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null object");
        }
    }

    private void tryGrowArray(int len) {
        if (offset + len >= buffer.length - 1) {
            growArray(len);
        }
    }

    private void growArray(int len) {
        byte[] bytes = new byte[buffer.length + len + GROW_ARRAY_CAPACITY];
        System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        buffer = bytes;
    }
}