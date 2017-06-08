package com.ironz.binaryprefs.serialization;

public interface Serializer<T> {

    /**
     * Serialize generic type into byte array with specific scheme.
     * Please see concrete implementation for explanation all available
     * schemes.
     *
     * @param t target instance type to serialize.
     * @return specific byte array by scheme.
     */
    byte[] serialize(T t);

    /**
     * Deserialize byte by {@link #serialize(Object)} convention
     *
     * @param key   token for determinate how to serialize
     *              one type of class type or interface type by two or more
     *              different serialization protocols.
     *              Default key is {@link #EMPTY_KEY}
     * @param bytes target byte array for deserialization
     * @return deserialized instance type
     */
    T deserialize(String key, byte[] bytes);

    /**
     * Deserialize byte by {@link #serialize(Object)} convention
     *
     * @param key    token for determinate how to serialize
     *               one type of class type or interface type by two or more
     *               different serialization protocols.
     *               Default key is {@link #EMPTY_KEY}
     * @param bytes  target byte array for deserialization
     * @param offset specific offset for using a long arrays
     * @param length from bytes array
     * @return deserialized instance type
     */
    T deserialize(String key, byte[] bytes, int offset, int length);

    /**
     * Checks and returns {@code true} if flag is matches
     * {@code false} if not.
     *
     * @param flag byte flag type
     * @return {@code true} or {@code false} if flag matches with serializer type
     */
    boolean isMatches(byte flag);

    /**
     * Checks and returns {@code true} if object class type is matches
     * {@code false} if not.
     *
     * @param o target object for serialization
     * @return {@code true} or {@code false} if object matches with serializer type
     */
    boolean isMatches(Object o);

    /**
     * Returns minimum of byte size for concrete type.
     * Especially for {@link java.lang.String} and {@link java.util.Set}
     * returns always {@code 1}.
     *
     * @return structure bytes size or minimum size of bytes for few types.
     */
    int bytesLength();

    /**
     * Empty key for determine empty object deserialization token
     */
    String EMPTY_KEY = "";
}