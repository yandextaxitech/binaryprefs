package com.ironz.binaryprefs.serialization.impl.persistable.io;

import com.ironz.binaryprefs.serialization.Serializer;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;

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

        this.buffer = bytes;

        checkBytes();
        checkNull(clazz);
        checkPersistable();

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
        checkType(booleanSerializer, buffer[offset]);
        int length = booleanSerializer.bytesLength();
        boolean b = booleanSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return b;
    }

    @Override
    public byte readByte() {
        checkBounds();
        checkType(byteSerializer, buffer[offset]);
        int length = byteSerializer.bytesLength();
        byte b = byteSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return b;
    }

    @Override
    public short readShort() {
        checkBounds();
        checkType(shortSerializer, buffer[offset]);
        int length = shortSerializer.bytesLength();
        short s = shortSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return s;
    }

    @Override
    public char readChar() {
        checkBounds();
        checkType(charSerializer, buffer[offset]);
        int length = charSerializer.bytesLength();
        char c = charSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return c;
    }

    @Override
    public int readInt() {
        checkBounds();
        checkType(integerSerializer, buffer[offset]);
        int length = integerSerializer.bytesLength();
        int i = integerSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return i;
    }

    @Override
    public long readLong() {
        checkBounds();
        checkType(longSerializer, buffer[offset]);
        int length = longSerializer.bytesLength();
        long l = longSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return l;
    }

    @Override
    public float readFloat() {
        checkBounds();
        checkType(floatSerializer, buffer[offset]);
        int length = floatSerializer.bytesLength();
        float f = floatSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return f;
    }

    @Override
    public double readDouble() {
        checkBounds();
        checkType(doubleSerializer, buffer[offset]);
        int length = doubleSerializer.bytesLength();
        double d = doubleSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;
        return d;
    }

    @Override
    public String readString() {
        checkBounds();
        checkType(integerSerializer, buffer[offset]);
        int length = integerSerializer.bytesLength();
        int bytesStringSize = integerSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, length);
        offset += length;

        checkBounds();
        checkType(stringSerializer, buffer[offset]);
        String s = stringSerializer.deserialize(Serializer.EMPTY_KEY, buffer, offset, bytesStringSize);
        offset += stringSerializer.bytesLength() + bytesStringSize;
        return s;
    }

    private void checkBounds() {
        if (offset >= buffer.length - 1) {
            throw new ArrayIndexOutOfBoundsException("Can't read out of bounds array");
        }
    }

    private void checkType(Serializer<?> serializer, byte flag) {
        if (!serializer.isMatches(flag)) {
            throw new ClassCastException(String.format("Can't deserialize flag type '%s' with '%s' serializer.", flag, serializer.getClass().getName()));
        }
    }

    private void checkBytes() {
        if (buffer.length < 1) {
            throw new UnsupportedOperationException("Cannot deserialize empty byte array!");
        }
    }

    private void checkNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null object");
        }
    }

    private void checkPersistable() {
        byte flag = buffer[0];
        if (flag != Persistable.FLAG_PERSISTABLE) {
            throw new ClassCastException(String.format("Persistable cannot be deserialized in '%s' flag type", flag));
        }
        offset++;
    }
}