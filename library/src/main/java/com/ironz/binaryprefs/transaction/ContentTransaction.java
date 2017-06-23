package com.ironz.binaryprefs.transaction;

public final class ContentTransaction {

    private final String name;
    private final byte[] content;

    public ContentTransaction(String name, byte[] content) {
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