package com.ironz.binaryprefs.serialization.impl.persistable.io;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;

public final class BinaryPrefsObjectInputImpl implements DataInput {

    private final BooleanSerializer booleanSerializer;
    private final ByteSerializerImpl byteSerializer;
    private final CharSerializerImpl charSerializer;
    private final DoubleSerializerImpl doubleSerializer;
    private final FloatSerializerImpl floatSerializer;
    private final IntegerSerializerImpl integerSerializer;
    private final LongSerializerImpl longSerializer;
    private final ShortSerializerImpl shortSerializer;
    private final StringSerializerImpl stringSerializer;

    private int offset = 0;
    private byte[] buffer;

    public BinaryPrefsObjectInputImpl(BooleanSerializer booleanSerializer,
                                      ByteSerializerImpl byteSerializer,
                                      CharSerializerImpl charSerializer,
                                      DoubleSerializerImpl doubleSerializer,
                                      FloatSerializerImpl floatSerializer,
                                      IntegerSerializerImpl integerSerializer,
                                      LongSerializerImpl longSerializer,
                                      ShortSerializerImpl shortSerializer,
                                      StringSerializerImpl stringSerializer) {
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
        byte integerFlag = buffer[offset];
        if (!integerSerializer.isMatches(integerFlag)) {
            throw new ClassCastException(String.format("string cannot be deserialized in '%s' flag type", integerFlag));
        }
        int length = integerSerializer.bytesLength();
        int bytesStringSize = integerSerializer.deserialize(buffer, offset);
        offset += length;

        checkBounds();
        byte stringFlag = buffer[offset];
        if (!stringSerializer.isMatches(stringFlag)) {
            throw new ClassCastException(String.format("boolean cannot be deserialized in '%s' flag type", stringFlag));
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

    private void checkPersistable() {
        byte flag = buffer[0];
        if (flag != Persistable.FLAG_PERSISTABLE) {
            throw new ClassCastException(String.format("Persistable cannot be deserialized in '%s' flag type", flag));
        }
        offset++;
    }
}