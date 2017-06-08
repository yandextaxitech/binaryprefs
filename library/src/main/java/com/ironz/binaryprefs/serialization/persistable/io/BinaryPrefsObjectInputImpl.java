package com.ironz.binaryprefs.serialization.persistable.io;

import com.ironz.binaryprefs.serialization.Serializer;
import com.ironz.binaryprefs.serialization.persistable.Persistable;

public final class BinaryPrefsObjectInputImpl implements DataInput {

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
    private byte[] buffer;

    public BinaryPrefsObjectInputImpl(Serializer<Boolean> booleanSerializer,
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
    public <T extends Persistable> T deserialize(byte[] bytes, Class<T> clazz) {

        checkBytes(bytes);
        checkNull(clazz);

        this.buffer = bytes;
        offset++;

        T instance = newInstance(clazz);
        instance.readExternal(this);

        return instance;
    }

    private <T extends Persistable> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean readBoolean() {
        checkBounds();
        int length = booleanSerializer.bytesLength();
        boolean b = booleanSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return b;
    }

    @Override
    public byte readByte() {
        checkBounds();
        int length = byteSerializer.bytesLength();
        byte b = byteSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return b;
    }

    @Override
    public short readShort() {
        checkBounds();
        int length = shortSerializer.bytesLength();
        short s = shortSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return s;
    }

    @Override
    public char readChar() {
        checkBounds();
        int length = charSerializer.bytesLength();
        char c = charSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return c;
    }

    @Override
    public int readInt() {
        checkBounds();
        int length = integerSerializer.bytesLength();
        int i = integerSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return i;
    }

    @Override
    public long readLong() {
        checkBounds();
        int length = longSerializer.bytesLength();
        long l = longSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return l;
    }

    @Override
    public float readFloat() {
        checkBounds();
        int length = floatSerializer.bytesLength();
        float f = floatSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return f;
    }

    @Override
    public double readDouble() {
        checkBounds();
        int length = doubleSerializer.bytesLength();
        double d = doubleSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return d;
    }

    @Override
    public String readString() {
        checkBounds();
        int length = integerSerializer.bytesLength();
        int bytesStringSize = integerSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        String s = stringSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, bytesStringSize);
        offset += stringSerializer.bytesLength() + bytesStringSize;
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

    private void checkBytes(byte[] bytes) {
        if (bytes.length < 1) {
            throw new UnsupportedOperationException("Cannot deserialize empty byte array!");
        }
    }
}