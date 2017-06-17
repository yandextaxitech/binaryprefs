package com.ironz.binaryprefs.serialization.serializer.persistable.io;

import com.ironz.binaryprefs.serialization.serializer.*;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

public final class BinaryPrefsObjectInputImpl implements DataInput {

    private final BooleanSerializer booleanSerializer;
    private final ByteSerializer byteSerializer;
    private final CharSerializer charSerializer;
    private final DoubleSerializer doubleSerializer;
    private final FloatSerializer floatSerializer;
    private final IntegerSerializer integerSerializer;
    private final LongSerializer longSerializer;
    private final ShortSerializer shortSerializer;
    private final StringSerializer stringSerializer;

    private int offset = 0;
    private byte[] buffer;

    public BinaryPrefsObjectInputImpl(BooleanSerializer booleanSerializer,
                                      ByteSerializer byteSerializer,
                                      CharSerializer charSerializer,
                                      DoubleSerializer doubleSerializer,
                                      FloatSerializer floatSerializer,
                                      IntegerSerializer integerSerializer,
                                      LongSerializer longSerializer,
                                      ShortSerializer shortSerializer,
                                      StringSerializer stringSerializer) {
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

        buffer = bytes;

        checkBytes();
        checkNull(clazz);

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
        byte flag = buffer[offset];
        if (!booleanSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", flag));
        }
        int length = booleanSerializer.bytesLength();
        boolean b = booleanSerializer.deserialize(buffer, offset);
        offset += length;
        return b;
    }

    @Override
    public byte readByte() {
        checkBounds();
        byte flag = buffer[offset];
        if (!byteSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("byte cannot be deserialized in '%s' flag type", flag));
        }
        int length = byteSerializer.bytesLength();
        byte b = byteSerializer.deserialize(buffer, offset);
        offset += length;
        return b;
    }

    @Override
    public short readShort() {
        checkBounds();
        byte flag = buffer[offset];
        if (!shortSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("short cannot be deserialized in '%s' flag type", flag));
        }
        int length = shortSerializer.bytesLength();
        short s = shortSerializer.deserialize(buffer, offset);
        offset += length;
        return s;
    }

    @Override
    public char readChar() {
        checkBounds();
        byte flag = buffer[offset];
        if (!charSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("char cannot be deserialized in '%s' flag type", flag));
        }
        int length = charSerializer.bytesLength();
        char c = charSerializer.deserialize(buffer, offset);
        offset += length;
        return c;
    }

    @Override
    public int readInt() {
        checkBounds();
        byte flag = buffer[offset];
        if (!integerSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("int cannot be deserialized in '%s' flag type", flag));
        }
        int length = integerSerializer.bytesLength();
        int i = integerSerializer.deserialize(buffer, offset);
        offset += length;
        return i;
    }

    @Override
    public long readLong() {
        checkBounds();
        byte flag = buffer[offset];
        if (!longSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("long cannot be deserialized in '%s' flag type", flag));
        }
        int length = longSerializer.bytesLength();
        long l = longSerializer.deserialize(buffer, offset);
        offset += length;
        return l;
    }

    @Override
    public float readFloat() {
        checkBounds();
        byte flag = buffer[offset];
        if (!floatSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("float cannot be deserialized in '%s' flag type", flag));
        }
        int length = floatSerializer.bytesLength();
        float f = floatSerializer.deserialize(buffer, offset);
        offset += length;
        return f;
    }

    @Override
    public double readDouble() {
        checkBounds();
        byte flag = buffer[offset];
        if (!doubleSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("double cannot be deserialized in '%s' flag type", flag));
        }
        int length = doubleSerializer.bytesLength();
        double d = doubleSerializer.deserialize(buffer, offset);
        offset += length;
        return d;
    }

    @Override
    public String readString() {
        checkBounds();
        int bytesStringSize = readInt();
        byte stringFlag = buffer[offset];
        if (!stringSerializer.isMatches(stringFlag)) {
            throw new ClassCastException(String.format("string cannot be deserialized in '%s' flag type", stringFlag));
        }
        String s = stringSerializer.deserialize(buffer, offset, bytesStringSize);
        offset += stringSerializer.bytesLength() + bytesStringSize;
        return s;
    }

    private void checkBounds() {
        if (offset >= buffer.length - 1) {
            throw new ArrayIndexOutOfBoundsException("Can't read out of bounds array");
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
}