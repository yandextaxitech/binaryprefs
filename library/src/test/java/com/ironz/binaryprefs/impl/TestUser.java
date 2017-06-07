package com.ironz.binaryprefs.impl;

import com.ironz.binaryprefs.serialization.persistable.Persistable;
import com.ironz.binaryprefs.serialization.persistable.io.DataInput;
import com.ironz.binaryprefs.serialization.persistable.io.DataOutput;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public final class TestUser implements Persistable {

    public static final String KEY = "user";

    private String name;
    private short age;
    private char sex;
    private boolean married;
    private long postal;
    private byte child;
    private float weight;
    private float height;

    private final List<TestAddress> addresses = new ArrayList<>();

    public TestUser() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public void setPostal(long postal) {
        this.postal = postal;
    }

    public void setChild(byte child) {
        this.child = child;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setHeight(float height) {
        this.height = height;
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
        out.writeFloat(weight);
        out.writeFloat(height);

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
        weight = in.readFloat();
        height = in.readFloat();

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
        if (Float.compare(testUser.weight, weight) != 0) return false;
        if (Float.compare(testUser.height, height) != 0) return false;
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
        result = 31 * result + (weight != +0.0f ? Float.floatToIntBits(weight) : 0);
        result = 31 * result + (height != +0.0f ? Float.floatToIntBits(height) : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "name='" + name + '\'' + '\n' +
                ", age=" + age + '\n' +
                ", sex=" + sex + '\n' +
                ", married=" + married + '\n' +
                ", postal=" + postal + '\n' +
                ", child=" + child + '\n' +
                ", weight=" + weight + '\n' +
                ", height=" + height + '\n' +
                ", addresses=" + addresses + '\n' +
                '}';
    }

    public static TestUser createUser() {
        TestUser value = new TestUser();
        value.setName("John");
        value.setAge((short) 21);
        value.setSex('M');
        value.setMarried(true);
        value.setPostal(1234567890L);
        value.setChild((byte) 19);
        value.setWeight(74.2f);
        value.setHeight(1.78f);
        value.addAddresses(new TestAddress("USA", "New York", "1th", 25, 53.123, 35.098));
        value.addAddresses(new TestAddress("Russia", "Moscow", "Red Square", 1, 53.123, 35.098));
        return value;
    }
}