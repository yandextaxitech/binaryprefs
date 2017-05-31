package com.ironz.binaryprefs.serialization;

import org.junit.Test;

import java.io.ObjectOutput;

public class BinaryPrefsObjectOutputImplTest {

    private final ObjectOutput objectOutput = new BinaryPrefsObjectOutputImpl();

    @Test(expected = UnsupportedOperationException.class)
    public void writeObjectNotExternalizable() throws Exception {
        objectOutput.writeObject("");
    }
}