package com.ironz.binaryprefs.task.barrierprovider.impl;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;
import com.ironz.binaryprefs.task.barrier.impl.InterruptableFutureBarrier;
import com.ironz.binaryprefs.task.barrierprovider.FutureBarrierProvider;

import java.util.concurrent.Future;

public final class InterruptableFutureBarrierProvider implements FutureBarrierProvider {

    @Override
    public <T> FutureBarrier<T> get(Future<T> submit, ExceptionHandler handler) {
        return new InterruptableFutureBarrier<>(submit, handler);
    }
}
