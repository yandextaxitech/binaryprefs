package com.ironz.binaryprefs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public final class TestDataClass implements Externalizable {

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