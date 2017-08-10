package com.ironz.binaryprefs.serialization.serializer.persistable.io;

import com.ironz.binaryprefs.serialization.serializer.*;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

public final class PersistableObjectInputImpl implements DataInput {

    private static final String BASE_INCORRECT_TYPE_MESSAGE = "cannot be deserialized in '%s' flag type";
    private static final String BASE_NOT_MIRRORED_MESSAGE = "May be your read/write contract isn't mirror-implemented or " +
            "old disk version is not backward compatible with new class version?";
    private static final String INCORRECT_BOOLEAN_MESSAGE = "boolean " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_BYTE_MESSAGE = "byte " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_SHORT_MESSAGE = "short " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_CHAR_MESSAGE = "char " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_INT_MESSAGE = "int " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_LONG_MESSAGE = "long " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_FLOAT_MESSAGE = "float " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_DOUBLE_MESSAGE = "double " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_STRING_MESSAGE = "String " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String OUT_OF_BOUNDS_MESSAGE = "Can't read out of bounds array (%s bytes > %s bytes) for %s! " +
            BASE_NOT_MIRRORED_MESSAGE;
    private static final String EMPTY_BYTE_ARRAY_MESSAGE = "Cannot deserialize empty byte array for %s! " +
            BASE_NOT_MIRRORED_MESSAGE;
    private static final String NULL_OBJECT_MESSAGE = "Can't serialize null object";

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
    private Persistable instance;

    public PersistableObjectInputImpl(BooleanSerializer booleanSerializer,
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
    public void deserialize(byte[] bytes, Persistable instance) {

        this.buffer = bytes;
        this.instance = instance;

        checkBytes();
        checkNull();

        offset++;

        //noinspection unused
        int versionStub = readInt(); // TODO: 7/11/17 implement v.2 serialization protocol migration

        instance.readExternal(this);
    }

    @Override
    public boolean readBoolean() {
        int length = booleanSerializer.bytesLength();
        checkBounds(length);
        byte flag = buffer[offset];
        if (!booleanSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_BOOLEAN_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_BYTE_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_SHORT_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_CHAR_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_INT_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_LONG_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_FLOAT_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_DOUBLE_MESSAGE, flag));
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
            throw new ClassCastException(String.format(INCORRECT_STRING_MESSAGE, flag));
        }
        String s = stringSerializer.deserialize(buffer, offset, bytesStringSize);
        offset += length;
        return s;
    }

    private void checkBounds(int requiredLength) {
        int requiredBound = offset + requiredLength;
        int length = buffer.length;
        if (requiredBound > length) {
            String instanceClassName = instance.getClass().getName();
            throw new ArrayIndexOutOfBoundsException(String.format(OUT_OF_BOUNDS_MESSAGE, instanceClassName, requiredBound, length));
        }
    }

    private void checkBytes() {
        if (buffer.length < 1) {
            String instanceClassName = instance.getClass().getName();
            throw new UnsupportedOperationException(String.format(EMPTY_BYTE_ARRAY_MESSAGE, instanceClassName));
        }
    }

    private void checkNull() {
        if (instance == null) {
            throw new NullPointerException(NULL_OBJECT_MESSAGE);
        }
    }
}