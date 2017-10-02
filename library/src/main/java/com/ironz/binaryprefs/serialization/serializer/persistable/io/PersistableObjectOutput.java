package com.ironz.binaryprefs.serialization.serializer.persistable.io;

import com.ironz.binaryprefs.serialization.serializer.*;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

public final class PersistableObjectOutput implements DataOutput {

    //bytes capacity for initial array size, buffer array are resizable to (buffer.length + len + GROW_ARRAY_CAPACITY) * 2 after reaching limit.
    private static final int GROW_ARRAY_CAPACITY = 128;
    private static final int VERSION_STUB = 1; // TODO: 7/11/17 implement v.2 serialization protocol migration

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

    private int offset = 0;
    private byte[] buffer = new byte[GROW_ARRAY_CAPACITY];

    public PersistableObjectOutput(BooleanSerializer booleanSerializer,
                                   ByteSerializer byteSerializer,
                                   ByteArraySerializer byteArraySerializer,
                                   CharSerializer charSerializer,
                                   DoubleSerializer doubleSerializer,
                                   FloatSerializer floatSerializer,
                                   IntegerSerializer integerSerializer,
                                   LongSerializer longSerializer,
                                   ShortSerializer shortSerializer,
                                   StringSerializer stringSerializer) {
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
    }

    @Override
    public byte[] serialize(Persistable value) {
        this.offset = 0;
        this.buffer = new byte[GROW_ARRAY_CAPACITY];
        write(new byte[]{PersistableSerializer.FLAG});
        writeInt(VERSION_STUB); // TODO: 7/11/17 implement v.2 serialization protocol migration
        value.writeExternal(this);
        return trimFinalArray();
    }

    private byte[] trimFinalArray() {
        byte[] bytes = new byte[offset];
        System.arraycopy(buffer, 0, bytes, 0, offset);
        return bytes;
    }

    @Override
    public void writeBoolean(boolean value) {
        byte[] serialize = booleanSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeByte(byte value) {
        byte[] serialize = byteSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeByteArray(byte[] value) {
        if (value == null) {
            writeInt(-1);
            return;
        }
        byte[] serialize = byteArraySerializer.serialize(value);
        int length = serialize.length - byteArraySerializer.bytesLength();
        writeInt(length);
        write(serialize);
    }

    @Override
    public void writeShort(short value) {
        byte[] serialize = shortSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeChar(char value) {
        byte[] serialize = charSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeInt(int value) {
        byte[] serialize = integerSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeLong(long value) {
        byte[] serialize = longSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeFloat(float value) {
        byte[] serialize = floatSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeDouble(double value) {
        byte[] serialize = doubleSerializer.serialize(value);
        write(serialize);
    }

    @Override
    public void writeString(String value) {
        if (value == null) {
            writeInt(-1);
            return;
        }
        byte[] serialize = stringSerializer.serialize(value);
        int length = serialize.length - stringSerializer.bytesLength();
        writeInt(length);
        write(serialize);
    }

    private void write(byte[] value) {
        int length = value.length;
        tryGrowArray(length);
        System.arraycopy(value, 0, buffer, offset, length);
        offset += length;
    }

    private void tryGrowArray(int length) {
        boolean isOutOfBounds = offset + length >= buffer.length - 1;
        if (isOutOfBounds) {
            growArray(length);
        }
    }

    private void growArray(int length) {
        int newLength = (buffer.length + length + GROW_ARRAY_CAPACITY) * 2;
        byte[] bytes = new byte[newLength];
        System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        buffer = bytes;
    }
}