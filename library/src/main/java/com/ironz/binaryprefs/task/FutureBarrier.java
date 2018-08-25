package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.exception.FileOperationException;

import java.util.concurrent.Future;

/**
 * Meta object which holds current task state and allows blocking await.
 */
public final class FutureBarrier<T> {

    private final Future<T> future;
    private final ExceptionHandler exceptionHandler;

    FutureBarrier(Future<T> future, ExceptionHandler exceptionHandler) {
        this.future = future;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Complete task without exception handle and re-throws exception on higher level.
     */
    public void completeBlockingUnsafe() {
        try {
            future.get();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    /**
     * Complete task with exception handle and returns result or default value for this task.
     */
    public T completeBlockingWihResult(T defValue) {
        try {
            return future.get();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    /**
     * Complete task with result returning without exception handle and re-throws exception on higher level.
     */
    public T completeBlockingWithResultUnsafe() {
        try {
            return future.get();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    /**
     * Returns task execution result.
     * Also this method will call exception handle method if task execution fails.
     *
     * @return status - {@code true} if task completed successfully {@code false} otherwise
     */
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