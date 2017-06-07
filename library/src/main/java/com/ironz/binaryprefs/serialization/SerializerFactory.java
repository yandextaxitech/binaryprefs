package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.persistable.Persistable;
import com.ironz.binaryprefs.serialization.persistable.PersistableClassProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SerializerFactory {

    private final Map<String, Serializer> typeSerializerMap = new HashMap<>();
    private final Map<Byte, Serializer> flagSerializerMap = new HashMap<>();

    public static SerializerFactory create() {
        return new SerializerFactory();
    }

    private SerializerFactory() {

        final Serializer<Boolean> booleanSerializer = new BooleanSerializerImpl();
        final Serializer<Byte> byteSerializer = new ByteSerializerImpl();
        final Serializer<Character> charSerializer = new CharSerializerImpl();
        final Serializer<Double> doubleSerializer = new DoubleSerializerImpl();
        final Serializer<Float> floatSerializer = new FloatSerializerImpl();
        final Serializer<Integer> integerSerializer = new IntegerSerializerImpl();
        final Serializer<Long> longSerializer = new LongSerializerImpl();
        final Serializer<Short> shortSerializer = new ShortSerializerImpl();
        final Serializer<String> stringSerializer = new StringSerializerImpl();
        final Serializer<Set<String>> stringSetSerializer = new StringSetSerializerImpl();
        final PersistableClassProvider classProvider = new PersistableClassProvider();

        final Serializer<Persistable> persistableSerializer = new PersistableSerializerImpl(
                booleanSerializer,
                byteSerializer,
                charSerializer,
                doubleSerializer,
                floatSerializer,
                integerSerializer,
                longSerializer,
                shortSerializer,
                stringSerializer,
                stringSetSerializer,
                classProvider
        );

        typeSerializerMap.put(Boolean.class.getName(), booleanSerializer);
        typeSerializerMap.put(Byte.class.getName(), byteSerializer);
        typeSerializerMap.put(Character.class.getName(), charSerializer);
        typeSerializerMap.put(Double.class.getName(), doubleSerializer);
        typeSerializerMap.put(Float.class.getName(), floatSerializer);
        typeSerializerMap.put(Integer.class.getName(), integerSerializer);
        typeSerializerMap.put(Long.class.getName(), longSerializer);
        typeSerializerMap.put(Short.class.getName(), shortSerializer);
        typeSerializerMap.put(String.class.getName(), stringSerializer);
        typeSerializerMap.put(Set.class.getName(), stringSetSerializer);
        typeSerializerMap.put(Persistable.class.getName(), persistableSerializer);

        flagSerializerMap.put(booleanSerializer.getFlag(), booleanSerializer);
        flagSerializerMap.put(byteSerializer.getFlag(), byteSerializer);
        flagSerializerMap.put(charSerializer.getFlag(), charSerializer);
        flagSerializerMap.put(doubleSerializer.getFlag(), doubleSerializer);
        flagSerializerMap.put(floatSerializer.getFlag(), floatSerializer);
        flagSerializerMap.put(integerSerializer.getFlag(), integerSerializer);
        flagSerializerMap.put(longSerializer.getFlag(), longSerializer);
        flagSerializerMap.put(shortSerializer.getFlag(), shortSerializer);
        flagSerializerMap.put(stringSerializer.getFlag(), stringSerializer);
        flagSerializerMap.put(stringSetSerializer.getFlag(), stringSetSerializer);
        flagSerializerMap.put(persistableSerializer.getFlag(), persistableSerializer);
    }

    public <T> Serializer<T> getByClassType(String classType) {
        if (!typeSerializerMap.containsKey(classType)) {
            throw new UnsupportedClassVersionError(String.format("Type verification failed. Incorrect type '%s'", classType));
        }
        //noinspection unchecked
        return typeSerializerMap.get(classType);
    }

    public <T> Serializer<T> getByFlag(byte flag) {
        if (!flagSerializerMap.containsKey(flag)) {
            throw new UnsupportedClassVersionError(String.format("Flag verification failed. Incorrect flag '%s'", flag));
        }
        //noinspection unchecked
        return flagSerializerMap.get(flag);
    }
}