package com.ironz.binaryprefs.impl;


import com.ironz.binaryprefs.serialization.Persistable;
import com.ironz.binaryprefs.serialization.io.DataInput;
import com.ironz.binaryprefs.serialization.io.DataOutput;

@SuppressWarnings("WeakerAccess")
public final class TestAddress implements Persistable {

    private String country;
    private String city;
    private String street;
    private int apt;
    private double latitude;
    private double longitude;

    public TestAddress() {
    }

    public TestAddress(String country, String city, String street, int apt, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.apt = apt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getApt() {
        return apt;
    }

    public void setApt(int apt) {
        this.apt = apt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestAddress that = (TestAddress) o;

        if (apt != that.apt) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        return street != null ? street.equals(that.street) : that.street == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = country != null ? country.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + apt;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public void writeExternal(DataOutput out) {
        out.writeString(country);
        out.writeString(city);
        out.writeString(street);
        out.writeInt(apt);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    @Override
    public void readExternal(DataInput in) {
        country = in.readString();
        city = in.readString();
        street = in.readString();
        apt = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public String toString() {
        return "TestAddress{" +
                "country='" + country + '\'' + '\n' +
                ", city='" + city + '\'' + '\n' +
                ", street='" + street + '\'' + '\n' +
                ", apt=" + apt + '\n' +
                ", latitude=" + latitude + '\n' +
                ", longitude=" + longitude + '\n' +
                '}';
    }
}