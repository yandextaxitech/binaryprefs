package com.ironz.binaryprefs.serialization.persistable;

import com.ironz.binaryprefs.serialization.persistable.io.DataInput;
import com.ironz.binaryprefs.serialization.persistable.io.DataOutput;

import java.io.Serializable;

/**
 * Only the identity of the class of an Persistable instance is
 * written in the serialization stream and it is the responsibility
 * of the class to save and restore the contents of its instances.
 * <p>
 * The {@link #writeExternal(DataOutput)} and {@link #readExternal(DataInput)}
 * methods of the Persistable interface are implemented by a class to give
 * the class complete control over the format and contents of the stream
 * for an object and its supertypes.
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
     * Empty key for determine empty object deserialization token
     */
    String EMPTY_KEY = "";

    /**
     * Uses for detecting byte array primitive type of {@link Persistable}
     */
    byte FLAG_PERSISTABLE = -11;
}