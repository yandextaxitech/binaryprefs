package com.ironz.binaryprefs.serialization.serializer.persistable.io;

import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;

public interface DataOutput {

    /**
     * Writes a <code>boolean</code> value to this output stream.
     *
     * @param v the boolean to be written.
     */
    void writeBoolean(boolean v);

    /**
     * Writes to the output stream the eight low-
     * order bits of the argument <code>v</code>.
     *
     * @param v the <code>byte</code> value to be written.
     */
    void writeByte(byte v);

    /**
     * Writes to the output stream the eight low-
     * order bits array of the argument <code>v</code>.
     *
     * @param v the <code>byte[]</code> value to be written.
     */
    void writeByteArray(byte[] v);

    /**
     * Writes two bytes to the output
     * stream to represent the value of the argument.
     *
     * @param v the <code>short</code> value to be written.
     */
    void writeShort(short v);

    /**
     * Writes a <code>char</code> value, which
     * is comprised of two bytes, to the
     * output stream.
     *
     * @param v the <code>char</code> value to be written.
     */
    void writeChar(char v);

    /**
     * Writes an <code>int</code> value, which is
     * comprised of four bytes, to the output stream.
     *
     * @param v the <code>int</code> value to be written.
     */
    void writeInt(int v);

    /**
     * Writes a <code>long</code> value, which is
     * comprised of eight bytes, to the output stream.
     *
     * @param v the <code>long</code> value to be written.
     */
    void writeLong(long v);

    /**
     * Writes a <code>float</code> value,
     * which is comprised of four bytes, to the output stream.
     *
     * @param v the <code>float</code> value to be written.
     */
    void writeFloat(float v);

    /**
     * Writes a <code>double</code> value,
     * which is comprised of eight bytes, to the output stream.
     *
     * @param v the <code>double</code> value to be written.
     */
    void writeDouble(double v);

    /**
     * Writes a <code>String</code> value,
     * which is comprised of n bytes, to the output stream.
     *
     * @param s the <code>String</code> not null value to be written.
     */
    void writeString(String s);

    /**
     * Serializes all input object data into byte array with specific scheme
     *
     * @param value given object
     * @return byte array
     */
    byte[] serialize(Persistable value);
}
