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
        boolean b = booleanSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, booleanSerializer.bytesLength());
        offset += booleanSerializer.bytesLength();
        return b;
    }

    @Override
    public byte readByte() {
        checkBounds();
        byte b = byteSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, byteSerializer.bytesLength());
        offset += byteSerializer.bytesLength();
        return b;
    }

    @Override
    public short readShort() {
        checkBounds();
        short s = shortSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, shortSerializer.bytesLength());
        offset += shortSerializer.bytesLength();
        return s;
    }

    @Override
    public char readChar() {
        checkBounds();
        char c = charSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, charSerializer.bytesLength());
        offset += charSerializer.bytesLength();
        return c;
    }

    @Override
    public int readInt() {
        checkBounds();
        int i = integerSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, integerSerializer.bytesLength());
        offset += integerSerializer.bytesLength();
        return i;
    }

    @Override
    public long readLong() {
        checkBounds();
        long l = longSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, longSerializer.bytesLength());
        offset += longSerializer.bytesLength();
        return l;
    }

    @Override
    public float readFloat() {
        checkBounds();
        float f = floatSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, floatSerializer.bytesLength());
        offset += floatSerializer.bytesLength();
        return f;
    }

    @Override
    public double readDouble() {
        checkBounds();
        double d = doubleSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, doubleSerializer.bytesLength());
        offset += doubleSerializer.bytesLength();
        return d;
    }

    @Override
    public String readString() {
        checkBounds();
        int bytesStringSize = integerSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, integerSerializer.bytesLength());
        offset += integerSerializer.bytesLength();
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