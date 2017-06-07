package com.ironz.binaryprefs.serialization.persistable.io;

import com.ironz.binaryprefs.serialization.Serializer;
import com.ironz.binaryprefs.serialization.persistable.Persistable;

public final class BinaryPrefsObjectOutputImpl implements DataOutput {

    //bytes for initial array size, buffer array are resizable to (buffer.length + len + GROW_ARRAY_CAPACITY) * 2 after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 128;
    private final Serializer<Boolean> booleanSerializer;
    private final Serializer<Byte> byteSerializer;
    private final Serializer<Character> charSerializer;
    private final Serializer<Double> doubleSerializer;
    private final Serializer<Float> floatSerializer;
    private final Serializer<Integer> integerSerializer;
    private final Serializer<Long> longSerializer;
    private final Serializer<Short> shortSerializer;
    private final Serializer<String> stringSerializer;

    private int offset = 0;
    private byte[] buffer = new byte[GROW_ARRAY_CAPACITY];

    public BinaryPrefsObjectOutputImpl(Serializer<Boolean> booleanSerializer,
                                       Serializer<Byte> byteSerializer,
                                       Serializer<Character> charSerializer,
                                       Serializer<Double> doubleSerializer,
                                       Serializer<Float> floatSerializer,
                                       Serializer<Integer> integerSerializer,
                                       Serializer<Long> longSerializer,
                                       Serializer<Short> shortSerializer,
                                       Serializer<String> stringSerializer) {

        this.booleanSerializer = booleanSerializer;
        this.byteSerializer = byteSerializer;
        this.charSerializer = charSerializer;
        this.doubleSerializer = doubleSerializer;
        this.floatSerializer = floatSerializer;
        this.integerSerializer = integerSerializer;
        this.longSerializer = longSerializer;
        this.shortSerializer = shortSerializer;
        this.stringSerializer = stringSerializer;
    }

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
        write(booleanSerializer.serialize(value));
    }

    @Override
    public void writeByte(int value) {
        write(byteSerializer.serialize((byte) value));
    }

    @Override
    public void writeShort(int value) {
        write(shortSerializer.serialize((short) value));
    }

    @Override
    public void writeChar(int value) {
        write(charSerializer.serialize((char) value));
    }

    @Override
    public void writeInt(int value) {
        write(integerSerializer.serialize(value));
    }

    @Override
    public void writeLong(long value) {
        write(longSerializer.serialize(value));
    }

    @Override
    public void writeFloat(float value) {
        write(floatSerializer.serialize(value));
    }

    @Override
    public void writeDouble(double value) {
        write(doubleSerializer.serialize(value));
    }

    @Override
    public void writeString(String s) {
        write(integerSerializer.serialize(s.getBytes().length));
        write(stringSerializer.serialize(s));
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
        int newLength = (buffer.length + len + GROW_ARRAY_CAPACITY) * 2;
        byte[] bytes = new byte[newLength];
        System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        buffer = bytes;
    }
}