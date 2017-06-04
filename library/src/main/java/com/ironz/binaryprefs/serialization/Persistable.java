package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.io.DataInput;
import com.ironz.binaryprefs.serialization.io.DataOutput;

import java.io.Serializable;

/**
 * Only the identity of the class of an Persistable instance is
 * written in the serialization stream and it is the responsibility
 * of the class to save and restore the contents of its instances.
 * <p>
 * The writeExternal and readExternal methods of the Persistable
 * interface are implemented by a class to give the class complete
 * control over the format and contents of the stream for an object
 * and its supertypes. These methods must explicitly
 * coordinate with the supertype to save its state. These methods supersede
 * customized implementations of writeObject and readObject methods.<br>
 * <p>
 *
 * @see java.io.Serializable
 */
public interface Persistable extends Serializable {
    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values.
     *
     * @param out the stream to write the object to
     */
    void writeExternal(DataOutput out);

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types. The readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     */
    void readExternal(DataInput in);

    /**
     * Uses for detecting byte array primitive type of {@link Persistable}
     */
    byte FLAG_PERSISTABLE = -11;

    /**
     * Minimum size primitive type of {@link String}
     */
    int SIZE_STRING = 1;

    /**
     * Minimum size primitive type of {@link Integer}
     */
    int SIZE_INT = 5;

    /**
     * Minimum size primitive type of {@link Long}
     */
    int SIZE_LONG = 9;

    /**
     * Minimum size primitive type of {@link Double}
     */
    int SIZE_DOUBLE = 9;

    /**
     * Minimum size primitive type of {@link Float}
     */
    int SIZE_FLOAT = 5;

    /**
     * Minimum size primitive type of {@link Boolean}
     */
    int SIZE_BOOLEAN = 2;

    /**
     * Minimum size primitive type of {@link Byte}
     */
    int SIZE_BYTE = 2;

    /**
     * Minimum size primitive type of {@link Short}
     */
    int SIZE_SHORT = 3;

    /**
     * Minimum size primitive type of {@link Character}
     */
    int SIZE_CHAR = 3;
}