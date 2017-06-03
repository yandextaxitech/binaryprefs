package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.TestDataClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class BinaryPrefsObjectOutputImplTest {

    private BinaryPrefsObjectOutputImpl objectOutput;

    @Before
    public void setUp() {
        objectOutput = new BinaryPrefsObjectOutputImpl();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void writeObjectNotExternalizable() throws Exception {
        objectOutput.writeObject("");
    }

    @Test(expected = NullPointerException.class)
    public void writeObjectExternalizableNull() throws Exception {
        objectOutput.writeObject(null);
    }

    @Test
    public void writeObjectExternalizable() throws Exception {
        TestDataClass dataClass = new TestDataClass();
        dataClass.setB(Byte.MAX_VALUE);
        objectOutput.writeObject(dataClass);
    }

    @Test(expected = IOException.class)
    public void writeObjectExternalizableClosed() throws Exception {
        TestDataClass dataClass = new TestDataClass();
        dataClass.setB(Byte.MAX_VALUE);
        objectOutput.close();
        objectOutput.writeObject(dataClass);
    }
}