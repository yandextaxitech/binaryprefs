package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.util.concurrent.Future;

/**
 * Meta object which holds current task state for it's blocking await.
 */
public final class Completable {

    private final Future<?> submit;

    public Completable(Future<?> submit) {
        this.submit = submit;
    }

    /**
     * Returns task execution result
     *
     * @return {@code true} if task completed successfully {@code false} otherwise
     */
    public boolean completeBlocking() {
        try {
            submit.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 6/28/17 implement logging
        }
        return false;
    }

    /**
     * Complete task without exception handle and re-throws exception on higher level.
     */
    public void completeBlockingUnsafe() {
        try {
            submit.get();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }
}