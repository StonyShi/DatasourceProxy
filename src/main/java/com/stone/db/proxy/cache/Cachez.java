package com.stone.db.proxy.cache;

/**
 * Created by Stony on 2016/3/11.
 */
public interface Cachez<T> {

    public Integer set(String key,T value);

    public T get(String key);
}
