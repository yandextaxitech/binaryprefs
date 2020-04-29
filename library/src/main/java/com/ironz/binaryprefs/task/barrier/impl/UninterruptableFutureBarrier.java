package com.ironz.binaryprefs.task.barrier.impl;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Meta object which holds current task state and allows blocking await and respect thread interruption
 */

public final class UninterruptableFutureBarrier<T> implements FutureBarrier<T> {

    private final Future<T> future;
    private final ExceptionHandler exceptionHandler;

    public UninterruptableFutureBarrier(Future<T> future, ExceptionHandler exceptionHandler) {
        this.future = future;
        this.exceptionHandler = exceptionHandler;
    }

    public void completeBlockingUnsafe() {
        try {
            getUninterruptibly(future);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    public T completeBlockingWihResult(T defValue) {
        try {
            return getUninterruptibly(future);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    public T completeBlockingWithResultUnsafe() {
        try {
            return getUninterruptibly(future);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    public boolean completeBlockingWithStatus() {
        try {
            getUninterruptibly(future);
            return true;
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return false;
    }

    /**
     * Invokes {@link Future#get()} uninterruptibly.
     *
     * @throws ExecutionException    if the computation threw an exception
     * @throws CancellationException if the computation was cancelled
     */
    private static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
        boolean interrupted = false;

        try {
            while (true) {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
