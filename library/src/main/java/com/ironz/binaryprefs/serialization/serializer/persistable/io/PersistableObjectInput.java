package com.ironz.binaryprefs.serialization.serializer.persistable.io;

import com.ironz.binaryprefs.serialization.serializer.*;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;

public final class PersistableObjectInput implements DataInput {

    private static final String BASE_INCORRECT_TYPE_MESSAGE = "cannot be deserialized in '%s' flag type";
    private static final String BASE_NOT_MIRRORED_MESSAGE = "May be your read/write contract isn't mirror-implemented or " +
            "old disk version is not backward compatible with new class version?";
    private static final String INCORRECT_BOOLEAN_MESSAGE = "boolean " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_BYTE_MESSAGE = "byte " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_BYTE_ARRAY_MESSAGE = "byte array " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_SHORT_MESSAGE = "short " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_CHAR_MESSAGE = "char " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_INT_MESSAGE = "int " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_LONG_MESSAGE = "long " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_FLOAT_MESSAGE = "float " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_DOUBLE_MESSAGE = "double " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String INCORRECT_STRING_MESSAGE = "String " + BASE_INCORRECT_TYPE_MESSAGE;
    private static final String OUT_OF_BOUNDS_MESSAGE = "Can't read out of bounds array (expected size: %s bytes > disk size: %s bytes) for %s! key" +
            BASE_NOT_MIRRORED_MESSAGE;
    private static final String EMPTY_BYTE_ARRAY_MESSAGE = "Cannot deserialize empty byte array for %s key! " +
            BASE_NOT_MIRRORED_MESSAGE;

    private final BooleanSerializer booleanSerializer;
    private final ByteSerializer byteSerializer;
    private final ByteArraySerializer byteArraySerializer;
    private final CharSerializer charSerializer;
    private final DoubleSerializer doubleSerializer;
    private final FloatSerializer floatSerializer;
    private final IntegerSerializer integerSerializer;
    private final LongSerializer longSerializer;
    private final ShortSerializer shortSerializer;
    private final StringSerializer stringSerializer;
    private final PersistableRegistry persistableRegistry;

    private int offset = 0;
    private byte[] bytes;
    private String key;

    public PersistableObjectInput(BooleanSerializer booleanSerializer,
                                  ByteSerializer byteSerializer,
                                  ByteArraySerializer byteArraySerializer,
                                  CharSerializer charSerializer,
                                  DoubleSerializer doubleSerializer,
                                  FloatSerializer floatSerializer,
                                  IntegerSerializer integerSerializer,
                                  LongSerializer longSerializer,
                                  ShortSerializer shortSerializer,
                                  StringSerializer stringSerializer,
                                  PersistableRegistry persistableRegistry) {
        this.booleanSerializer = booleanSerializer;
        this.byteSerializer = byteSerializer;
        this.byteArraySerializer = byteArraySerializer;
        this.charSerializer = charSerializer;
        this.doubleSerializer = doubleSerializer;
        this.floatSerializer = floatSerializer;
        this.integerSerializer = integerSerializer;
        this.longSerializer = longSerializer;
        this.shortSerializer = shortSerializer;
        this.stringSerializer = stringSerializer;
        this.persistableRegistry = persistableRegistry;
    }

    @Override
    public Persistable deserialize(String key, byte[] bytes) {
        this.offset = 0;
        this.key = key;
        this.bytes = bytes;
        checkBytes();
        skipPersistableFlag();
        //noinspection unused
        int versionStub = readInt(); // TODO: 7/11/17 implement v.2 serialization protocol migration
        return constructPersistable();
    }

    private Persistable constructPersistable() {
        Class<? extends Persistable> clazz = persistableRegistry.get(key);
        Persistable instance = newInstance(clazz);
        instance.readExternal(this);
        return instance;
    }

    private void checkBytes() {
        if (bytes.length == 0) {
            throw new UnsupportedOperationException(String.format(EMPTY_BYTE_ARRAY_MESSAGE, key));
        }
    }

    private void skipPersistableFlag() {
        offset++;
    }

    private Persistable newInstance(Class<? extends Persistable> clazz) {
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
        byte flag = bytes[offset];
        if (!booleanSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_BOOLEAN_MESSAGE, flag));
        }
        boolean b = booleanSerializer.deserialize(bytes, offset);
        offset += length;
        return b;
    }

    @Override
    public byte readByte() {
        int length = byteSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!byteSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_BYTE_MESSAGE, flag));
        }
        byte b = byteSerializer.deserialize(bytes, offset);
        offset += length;
        return b;
    }

    @Override
    public byte[] readByteArray() {
        int bytesArraySize = readInt();
        if (bytesArraySize == -1) {
            return null;
        }
        int length = byteArraySerializer.bytesLength() + bytesArraySize;
        checkBounds(length);
        byte flag = bytes[offset];
        if (!byteArraySerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_BYTE_ARRAY_MESSAGE, flag));
        }
        byte[] a = byteArraySerializer.deserialize(bytes, offset, bytesArraySize);
        offset += length;
        return a;
    }

    @Override
    public short readShort() {
        int length = shortSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!shortSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_SHORT_MESSAGE, flag));
        }
        short s = shortSerializer.deserialize(bytes, offset);
        offset += length;
        return s;
    }

    @Override
    public char readChar() {
        int length = charSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!charSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_CHAR_MESSAGE, flag));
        }
        char c = charSerializer.deserialize(bytes, offset);
        offset += length;
        return c;
    }

    @Override
    public int readInt() {
        int length = integerSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!integerSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_INT_MESSAGE, flag));
        }
        int i = integerSerializer.deserialize(bytes, offset);
        offset += length;
        return i;
    }

    @Override
    public long readLong() {
        int length = longSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!longSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_LONG_MESSAGE, flag));
        }
        long l = longSerializer.deserialize(bytes, offset);
        offset += length;
        return l;
    }

    @Override
    public float readFloat() {
        int length = floatSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!floatSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_FLOAT_MESSAGE, flag));
        }
        float f = floatSerializer.deserialize(bytes, offset);
        offset += length;
        return f;
    }

    @Override
    public double readDouble() {
        int length = doubleSerializer.bytesLength();
        checkBounds(length);
        byte flag = bytes[offset];
        if (!doubleSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_DOUBLE_MESSAGE, flag));
        }
        double d = doubleSerializer.deserialize(bytes, offset);
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
        byte flag = bytes[offset];
        if (!stringSerializer.isMatches(flag)) {
            throw new ClassCastException(String.format(INCORRECT_STRING_MESSAGE, flag));
        }
        String s = stringSerializer.deserialize(bytes, offset, bytesStringSize);
        offset += length;
        return s;
    }

    private void checkBounds(int requiredLength) {
        int requiredBound = offset + requiredLength;
        int length = bytes.length;
        if (requiredBound > length) {
            throw new ArrayIndexOutOfBoundsException(String.format(OUT_OF_BOUNDS_MESSAGE, key, requiredBound, length));
        }
    }
}