package com.ironz.binaryprefs.transaction;

public final class ConcurrentContentTransactionExecutorImpl implements ContentTransactionExecutor {


    @Override
    public boolean performTransaction(ContentTransaction transaction) {
        return false;
    }
}
