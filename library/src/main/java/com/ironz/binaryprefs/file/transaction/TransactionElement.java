package com.ironz.binaryprefs.file.transaction;

public final class TransactionElement {

    private static final byte[] EMPTY_CONTENT = new byte[0];

    public static final int ACTION_UPDATE = 1;
    public static final int ACTION_REMOVE = 2;
    public static final int ACTION_FETCH = 3;

    private final int action;
    private final String name;
    private final byte[] content;

    public TransactionElement(int action, String name, byte[] content) {
        this.action = action;
        this.name = name;
        this.content = content;
    }

    public TransactionElement(int action, String name) {
        this.action = action;
        this.name = name;
        this.content = EMPTY_CONTENT;
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