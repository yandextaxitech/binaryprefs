package com.ironz.binaryprefs.file.transaction;

import android.os.Parcel;
import android.os.Parcelable;

public final class ParcelableFileTransactionElement implements Parcelable {

    static final int ACTION_UPDATE = 1;
    static final int ACTION_REMOVE = 2;

    private final int action;
    private final String name;
    private final byte[] content;

    public ParcelableFileTransactionElement(int action, String name, byte[] content) {
        this.action = action;
        this.name = name;
        this.content = content;
    }

    byte[] getContent() {
        return content;
    }

    int getAction() {
        return action;
    }

    String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action);
        dest.writeString(this.name);
        dest.writeByteArray(this.content);
    }

    protected ParcelableFileTransactionElement(Parcel in) {
        this.action = in.readInt();
        this.name = in.readString();
        this.content = in.createByteArray();
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