package com.ironz.binaryprefs.serialization;

import org.junit.Test;

import java.io.ObjectOutput;

public class ExternalizableObjectOutputImplTest {

    private final ObjectOutput objectOutput = new ExternalizableObjectOutputImpl();

    @Test(expected = UnsupportedOperationException.class)
    public void writeObjectNotExternalizable() throws Exception {
        objectOutput.writeObject("");
    }
}