package com.ironz.binaryprefs.file.transaction;

public final class FileTransactionElement {

    private final String name;
    private final byte[] content;

    public FileTransactionElement(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}