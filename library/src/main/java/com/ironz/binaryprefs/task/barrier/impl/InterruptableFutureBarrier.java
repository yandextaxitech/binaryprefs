package com.ironz.binaryprefs.task.barrier.impl;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;

import java.util.concurrent.Future;

/**
 * Meta object which holds current task state and allows blocking await
 */

public final class InterruptableFutureBarrier<T> implements FutureBarrier<T> {

    private final Future<T> future;
    private final ExceptionHandler exceptionHandler;

    public InterruptableFutureBarrier(Future<T> future, ExceptionHandler exceptionHandler) {
        this.future = future;
        this.exceptionHandler = exceptionHandler;
    }

    public void completeBlockingUnsafe() {
        try {
            future.get();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    public T completeBlockingWihResult(T defValue) {
        try {
            return future.get();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    public T completeBlockingWithResultUnsafe() {
        try {
            return future.get();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    public boolean completeBlockingWithStatus() {
        try {
            future.get();
            return true;
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return false;
    }
}
