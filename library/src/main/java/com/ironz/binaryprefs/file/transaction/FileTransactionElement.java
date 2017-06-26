package com.ironz.binaryprefs.file.transaction;

import android.os.Parcel;
import android.os.Parcelable;

public final class FileTransactionElement implements Parcelable {

    private static final byte[] EMPTY_CONTENT = new byte[0];

    static final int ACTION_UPDATE = 1;
    static final int ACTION_REMOVE = 2;
    static final int ACTION_FETCH = 3;

    private final int action;
    private final String name;
    private final byte[] content;

    public FileTransactionElement(int action, String name, byte[] content) {
        this.action = action;
        this.name = name;
        this.content = content;
    }

    public FileTransactionElement(int action, String name) {
        this.action = action;
        this.name = name;
        this.content = EMPTY_CONTENT;
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

    protected FileTransactionElement(Parcel in) {
        this.action = in.readInt();
        this.name = in.readString();
        this.content = in.createByteArray();
    }

    public static final Creator<FileTransactionElement> CREATOR = new Creator<FileTransactionElement>() {
        @Override
        public FileTransactionElement createFromParcel(Parcel source) {
            return new FileTransactionElement(source);
        }

        @Override
        public FileTransactionElement[] newArray(int size) {
            return new FileTransactionElement[size];
        }
    };
}