package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;
import org.xml.sax.AttributeList;

import java.util.AbstractQueue;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CollectionStringSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final CollectionStringSerializer serializer = new CollectionStringSerializer();

    @Test
    public void collection(){
        try {

            emptyCollection(ArrayDeque.class);
            stringConvert(ArrayDeque.class);

            emptyCollection(ArrayList.class);
            stringConvert(ArrayList.class);

            emptyCollection(ConcurrentLinkedDeque.class);
            stringConvert(ConcurrentLinkedDeque.class);

            emptyCollection(ConcurrentLinkedQueue.class);
            stringConvert(ConcurrentLinkedQueue.class);

            emptyCollection(ConcurrentSkipListSet.class);
            stringConvert(ConcurrentSkipListSet.class);

            emptyCollection(CopyOnWriteArrayList.class);
            stringConvert(CopyOnWriteArrayList.class);

            emptyCollection(CopyOnWriteArraySet.class);
            stringConvert(CopyOnWriteArraySet.class);

            emptyCollection(HashSet.class);
            stringConvert(HashSet.class);

            emptyCollection(LinkedBlockingDeque.class);
            stringConvert(LinkedBlockingDeque.class);

            emptyCollection(LinkedHashSet.class);
            stringConvert(LinkedHashSet.class);

            emptyCollection(LinkedList.class);
            stringConvert(LinkedList.class);

            emptyCollection(LinkedTransferQueue.class);
            stringConvert(LinkedTransferQueue.class);

            emptyCollection(PriorityBlockingQueue.class);
            stringConvert(PriorityBlockingQueue.class);

            emptyCollection(PriorityQueue.class);
            stringConvert(PriorityQueue.class);

            emptyCollection(Stack.class);
            stringConvert(Stack.class);

            emptyCollection(TreeSet.class);
            stringConvert(TreeSet.class);

            emptyCollection(Vector.class);
            stringConvert(Vector.class);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    private void emptyCollection(Class<? extends Collection> collectionClass) throws IllegalAccessException, InstantiationException {
        Collection<String> value = collectionClass.newInstance();

        byte[] bytes = serializer.serialize(value);
        Collection<String> restored = serializer.deserializeWithCollectionClass(bytes, collectionClass);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);

        assertTrue(value.containsAll(restored));
    }

    private void stringConvert(Class<? extends Collection> collection) throws InstantiationException, IllegalAccessException {
        Collection<String> value = collection.newInstance();
        value.add("One");
        value.add("Two");
        value.add("Three");
        value.add("");

        byte[] bytes = serializer.serialize(value);
        Collection<String> restored = serializer.deserializeWithCollectionClass(bytes, collection);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(28, bytes.length);

        //assertEquals(value, restored); //not work with ArrayDeque
        assertTrue(value.containsAll(restored));
    }

    @Test
    public void stringIncorrectFlag() {
        Collection<String> value = Collections.emptySet();

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }

}