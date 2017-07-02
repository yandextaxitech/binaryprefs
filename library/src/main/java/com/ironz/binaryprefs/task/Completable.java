package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.exception.FileOperationException;

import java.util.concurrent.Future;

/**
 * Meta object which holds current task state for it's blocking await.
 */
public final class Completable {

    private final Future<?> submit;
    private final ExceptionHandler exceptionHandler;

    public Completable(Future<?> submit, ExceptionHandler exceptionHandler) {
        this.submit = submit;
        this.exceptionHandler = exceptionHandler;
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
            exceptionHandler.handle(e);
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