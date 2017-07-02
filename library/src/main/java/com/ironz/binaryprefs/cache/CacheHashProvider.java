package com.ironz.binaryprefs.cache;

import android.util.Pair;

import java.util.Map;

/**
 * Describes contract which store, fetch and remove cached elements
 */
public interface CacheHashProvider extends CacheProvider{

    void putWithHash(String key, Object value, int hashCode);

    int getHash(String key);
}