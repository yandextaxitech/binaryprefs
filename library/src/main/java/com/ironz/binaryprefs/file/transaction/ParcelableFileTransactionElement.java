package com.ironz.binaryprefs.file.transaction;

import android.os.Parcel;
import android.os.Parcelable;

public final class ParcelableFileTransactionElement implements Parcelable {

    private final String name;
    private final byte[] content;

    @SuppressWarnings("WeakerAccess")
    protected ParcelableFileTransactionElement(Parcel in) {
        this.name = in.readString();
        this.content = in.createByteArray();
    }

    public String getName() {
        return name;
    }


    public byte[] getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByteArray(this.content);
    }

    public static final Creator<ParcelableFileTransactionElement> CREATOR = new Creator<ParcelableFileTransactionElement>() {
        @Override
        public ParcelableFileTransactionElement createFromParcel(Parcel source) {
            return new ParcelableFileTransactionElement(source);
        }

        @Override
        public ParcelableFileTransactionElement[] newArray(int size) {
            return new ParcelableFileTransactionElement[size];
        }
    };
}