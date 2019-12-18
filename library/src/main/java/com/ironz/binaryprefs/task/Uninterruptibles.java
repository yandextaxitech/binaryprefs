package com.ironz.binaryprefs.task;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final class Uninterruptibles {

    private Uninterruptibles() {
    }

    /**
     * Invokes {@link Future#get()} uninterruptibly.
     *
     * @throws ExecutionException    if the computation threw an exception
     * @throws CancellationException if the computation was cancelled
     */
    static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
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
