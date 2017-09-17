package com.ironz.binaryprefs.init;

public interface FetchStrategy {
    Object getValue(String key, Object defValue);
}