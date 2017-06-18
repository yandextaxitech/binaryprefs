package com.ironz.binaryprefs.serialization.strategy;

/**
 * Contract which describes serialization strategy. Strategy means
 * that can put value between constructor or another one method and
 * serialize it into byte array or get original object value.
 */
public interface SerializationStrategy {
    /**
     * Serializes value into byte array by specific serializer.
     *
     * @return target byte array of serialized object
     */
    byte[] serialize();

    /**
     * Returns the original value which has been defined from
     * constructor or another one method.
     *
     * @return target object for serialization.
     */
    Object getValue();
}