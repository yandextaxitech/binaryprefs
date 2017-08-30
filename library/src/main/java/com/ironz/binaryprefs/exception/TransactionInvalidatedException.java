package com.ironz.binaryprefs.exception;

import com.ironz.binaryprefs.PreferencesEditor;

/**
 * Exception will be thrown if {@link PreferencesEditor#apply()} or {@link PreferencesEditor#commit()}
 * called twice for one instance of {@link PreferencesEditor}.
 */
public final class TransactionInvalidatedException extends RuntimeException {

    private static final String MESSAGE = "Transaction should be applied or committed only once!";

    public TransactionInvalidatedException() {
        super(MESSAGE);
    }
}