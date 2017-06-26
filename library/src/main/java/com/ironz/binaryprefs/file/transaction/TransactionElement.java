package com.ironz.binaryprefs.file.transaction;

public final class TransactionElement {

    static final int ACTION_UPDATE = 1;
    static final int ACTION_REMOVE = 2;

    private final int action;
    private final String name;
    private final byte[] content;

    public TransactionElement(int action, String name, byte[] content) {
        this.action = action;
        this.name = name;
        this.content = content;
    }

    public int getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}