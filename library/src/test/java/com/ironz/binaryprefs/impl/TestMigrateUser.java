package com.ironz.binaryprefs.impl;

import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.io.DataInput;
import com.ironz.binaryprefs.serialization.serializer.persistable.io.DataOutput;

import java.util.ArrayList;
import java.util.List;

public final class TestMigrateUser implements Persistable {

    public static final String KEY = "migrate_user";

    private String name;
    private byte age;
    private char sex;
    private boolean married;
    private long postal;
    private short child;
    private float weight;
    private float height;

    private final List<TestAddress> addresses = new ArrayList<>();

    public TestMigrateUser() {
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setAge(byte age) {
        this.age = age;
    }

    private void setSex(char sex) {
        this.sex = sex;
    }

    private void setMarried(boolean married) {
        this.married = married;
    }

    private void setPostal(long postal) {
        this.postal = postal;
    }

    private void setChild(short child) {
        this.child = child;
    }

    private void setWeight(float weight) {
        this.weight = weight;
    }

    private void setHeight(float height) {
        this.height = height;
    }

    private void addAddresses(TestAddress address) {
        this.addresses.add(address);
    }

    @Override
    public void writeExternal(DataOutput out) {
        out.writeString(name);
        out.writeByte(age);
        out.writeChar(sex);
        out.writeBoolean(married);
        out.writeLong(postal);
        out.writeShort(child);
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
        age = in.readByte();
        sex = in.readChar();
        married = in.readBoolean();
        postal = in.readLong();
        child = in.readShort();
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
    public TestMigrateUser deepCopy() {
        TestMigrateUser value = new TestMigrateUser();
        value.setName(name);
        value.setAge(age);
        value.setSex(sex);
        value.setMarried(married);
        value.setPostal(postal);
        value.setChild(child);
        value.setWeight(weight);
        value.setHeight(height);
        for (TestAddress address : addresses) {
            TestAddress persistable = address.deepCopy();
            value.addAddresses(persistable);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestMigrateUser that = (TestMigrateUser) o;

        if (age != that.age) return false;
        if (sex != that.sex) return false;
        if (married != that.married) return false;
        if (postal != that.postal) return false;
        if (child != that.child) return false;
        if (Float.compare(that.weight, weight) != 0) return false;
        if (Float.compare(that.height, height) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return addresses != null ? addresses.equals(that.addresses) : that.addresses == null;
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
        return "TestMigrateUser{" +
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

    public static TestMigrateUser create() {
        TestMigrateUser value = new TestMigrateUser();
        value.setName("John");
        value.setAge((byte) 21);
        value.setSex('M');
        value.setMarried(true);
        value.setPostal(1234567890L);
        value.setChild((short) 19);
        value.setWeight(74.2f);
        value.setHeight(1.78f);
        value.addAddresses(new TestAddress("USA", "New York", "1th", 25, 53.123, 35.098));
        value.addAddresses(new TestAddress("Russia", "Moscow", "Red Square", 1, 53.123, 35.098));
        return value;
    }
}
