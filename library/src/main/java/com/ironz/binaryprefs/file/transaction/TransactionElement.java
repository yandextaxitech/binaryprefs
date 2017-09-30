package com.ironz.binaryprefs.file.transaction;

public final class TransactionElement {

    private static final byte[] EMPTY_CONTENT = {};

    private static final int ACTION_FETCH = 1;
    public static final int ACTION_UPDATE = 2;
    public static final int ACTION_REMOVE = 3;

    private final int action;
    private final String name;
    private final byte[] content;

    static TransactionElement createFetchElement(String name, byte[] content) {
        return new TransactionElement(ACTION_FETCH, name, content);
    }

    public static TransactionElement createUpdateElement(String name, byte[] content) {
        return new TransactionElement(ACTION_UPDATE, name, content);
    }

    public static TransactionElement createRemovalElement(String name) {
        return new TransactionElement(ACTION_REMOVE, name, EMPTY_CONTENT);
    }

    private TransactionElement(int action, String name, byte[] content) {
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