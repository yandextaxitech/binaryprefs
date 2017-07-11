package com.ironz.binaryprefs.exception;

/**
 * Exception will be thrown if preferences initialization has been performed on background thread.
 */
public final class PreferencesInitializationException extends RuntimeException {

    public PreferencesInitializationException(String msg) {
        super(msg);
    }
}