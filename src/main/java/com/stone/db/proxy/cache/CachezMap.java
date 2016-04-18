package com.stone.db.proxy.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stony on 2016/3/11.
 */
public class CachezMap implements Cachez{

    public static final ConcurrentHashMap<String,Object> CACHES = new ConcurrentHashMap<String,Object>();

    public Integer set(String key, Object value) {
        CACHES.put(key,value);
        return 1;
    }

    @Override
    public Object get(String key) {
        return CACHES.get(key);
    }
}
