package com.ironz.binaryprefs.file.transaction;

public final class FileElement {

    private final String name;
    private final byte[] content;

    public FileElement(String name, byte[] content) {
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