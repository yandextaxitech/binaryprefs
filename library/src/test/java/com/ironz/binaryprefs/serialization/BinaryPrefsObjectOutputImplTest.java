package com.ironz.binaryprefs.serialization;

import org.junit.Before;
import org.junit.Test;

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

}