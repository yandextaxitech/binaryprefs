package com.ironz.binaryprefs.impl;

import com.ironz.binaryprefs.serialization.Persistable;
import com.ironz.binaryprefs.serialization.io.DataInput;
import com.ironz.binaryprefs.serialization.io.DataOutput;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public final class TestUser implements Persistable {

    private String name;
    private short age;
    private char sex;
    private boolean married;
    private long postal;
    private byte child;
    private List<TestAddress> addresses = new ArrayList<>();

    public TestUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public long getPostal() {
        return postal;
    }

    public void setPostal(long postal) {
        this.postal = postal;
    }

    public byte getChild() {
        return child;
    }

    public void setChild(byte child) {
        this.child = child;
    }

    public List<TestAddress> getAddresses() {
        return addresses;
    }

    public void addAddresses(TestAddress address) {
        this.addresses.add(address);
    }

    @Override
    public void writeExternal(DataOutput out) {

        out.writeString(name);
        out.writeShort(age);
        out.writeChar(sex);
        out.writeBoolean(married);
        out.writeLong(postal);
        out.writeByte(child);

        int size = addresses.size();
        out.writeInt(size);
        for (TestAddress address : addresses) {
            address.writeExternal(out);
        }
    }

    @Override
    public void readExternal(DataInput in) {

        name = in.readString();
        age = in.readShort();
        sex = in.readChar();
        married = in.readBoolean();
        postal = in.readLong();
        child = in.readByte();

        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            TestAddress address = new TestAddress();
            address.readExternal(in);
            addresses.add(address);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestUser testUser = (TestUser) o;

        if (age != testUser.age) return false;
        if (sex != testUser.sex) return false;
        if (married != testUser.married) return false;
        if (postal != testUser.postal) return false;
        if (child != testUser.child) return false;
        if (name != null ? !name.equals(testUser.name) : testUser.name != null) return false;
        return addresses != null ? addresses.equals(testUser.addresses) : testUser.addresses == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) age;
        result = 31 * result + (int) sex;
        result = 31 * result + (married ? 1 : 0);
        result = 31 * result + (int) (postal ^ (postal >>> 32));
        result = 31 * result + (int) child;
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", married=" + married +
                ", postal=" + postal +
                ", child=" + child +
                ", addresses=" + addresses +
                '}';
    }
}