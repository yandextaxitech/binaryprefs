package com.ironz.binaryprefs.task.barrierprovider;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.task.barrier.FutureBarrier;

import java.util.concurrent.Future;

public interface FutureBarrierProvider {
    <T> FutureBarrier<T> get(Future<T> submit, ExceptionHandler handler);
}
