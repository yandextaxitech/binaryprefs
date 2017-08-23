package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.exception.FileOperationException;

import java.util.concurrent.Future;

/**
 * Meta object which holds current task state and allows blocking await.
 */
public final class FutureBarrier {

    private final Future<?> future;
    private final ExceptionHandler exceptionHandler;

    FutureBarrier(Future<?> future, ExceptionHandler exceptionHandler) {
        this.future = future;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Returns task execution result.
     * Also this method will call exception handle method if task execution fails.
     *
     * @return {@code true} if task completed successfully {@code false} otherwise
     */
    public boolean completeBlocking() {
        try {
            future.get();
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
            future.get();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    /**
     * Complete task with exception handle and returns result or default value for this task.
     */
    public Object completeBlockingWihResult(Object defValue) {
        try {
            return future.get();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }
}