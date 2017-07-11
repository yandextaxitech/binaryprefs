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

        //noinspection unused
        int versionStub = readInt(); // TODO: 7/11/17 implement version migration

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
        int length = booleanSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!booleanSerializer.isMatches(flag)) {
            throw new ClassCastException(
                    String.format("boolean cannot be deserialized in '%s' flag type", flag));
        }
        boolean b = booleanSerializer.deserialize(buffer, offset);
        offset += length;
        return b;
    }

    @Override
    public byte readByte() {
        int length = byteSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!byteSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("byte cannot be deserialized in '%s' flag type", flag));
        }
        byte b = byteSerializer.deserialize(buffer, offset);
        offset += length;
        return b;
    }

    @Override
    public short readShort() {
        int length = shortSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!shortSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("short cannot be deserialized in '%s' flag type", flag));
        }
        short s = shortSerializer.deserialize(buffer, offset);
        offset += length;
        return s;
    }

    @Override
    public char readChar() {
        int length = charSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!charSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("char cannot be deserialized in '%s' flag type", flag));
        }
        char c = charSerializer.deserialize(buffer, offset);
        offset += length;
        return c;
    }

    @Override
    public int readInt() {
        int length = integerSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!integerSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("int cannot be deserialized in '%s' flag type", flag));
        }
        int i = integerSerializer.deserialize(buffer, offset);
        offset += length;
        return i;
    }

    @Override
    public long readLong() {
        int length = longSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!longSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("long cannot be deserialized in '%s' flag type", flag));
        }
        long l = longSerializer.deserialize(buffer, offset);
        offset += length;
        return l;
    }

    @Override
    public float readFloat() {
        int length = floatSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!floatSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("float cannot be deserialized in '%s' flag type", flag));
        }
        float f = floatSerializer.deserialize(buffer, offset);
        offset += length;
        return f;
    }

    @Override
    public double readDouble() {
        int length = doubleSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!doubleSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("double cannot be deserialized in '%s' flag type", flag));
        }
        double d = doubleSerializer.deserialize(buffer, offset);
        offset += length;
        return d;
    }

    @Override
    public String readString() {
        int bytesStringSize = readInt();
        if (bytesStringSize == -1) {
            return null;
        }
        int length = stringSerializer.bytesLength() + bytesStringSize;
        checkBounds(length);
        byte flag = buffer[offset];
        if (!stringSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format("String cannot be deserialized in '%s' flag type", flag));
        }
        String s = stringSerializer.deserialize(buffer, offset, bytesStringSize);
        offset += length;
        return s;
    }

    private void checkBounds(int requiredLength) {
        int requiredBound = offset + requiredLength;
        int length = buffer.length;
        if (requiredBound > length) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format(
                            "Can't read out of bounds array (%s bytes > %s bytes). May be your read/write contract isn't mirror-implemented?",
                            requiredBound,
                            length
                    )
            );
        }
    }

    private void checkBytes() {
        if (buffer.length < 1) {
            throw new UnsupportedOperationException(
                    "Cannot deserialize empty byte array! May be your read/write contract isn't mirror-implemented?"
            );
        }
    }

    private void checkNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null object");
        }
    }
}