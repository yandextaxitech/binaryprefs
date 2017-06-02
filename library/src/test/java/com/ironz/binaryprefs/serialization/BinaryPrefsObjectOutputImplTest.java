package com.ironz.binaryprefs.serialization;

import org.junit.Test;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BinaryPrefsObjectOutputImplTest {

    private final BinaryPrefsObjectOutputImpl objectOutput = new BinaryPrefsObjectOutputImpl();

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
        SomeClass someClass = new SomeClass();
        someClass.setB(Byte.MAX_VALUE);
        objectOutput.writeObject(someClass);
    }

    private static class SomeClass implements Externalizable {

        private byte b;

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {

        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }

        public byte getB() {
            return b;
        }

        public void setB(byte b) {
            this.b = b;
        }
    }
}