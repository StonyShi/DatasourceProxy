package com.stone.db.proxy.cache;

/**
 * Created by Stony on 2016/3/11.
 */
public class CachezMapManager implements CachezManager {
    private Cachez cachez = new CachezMap();
    private Cachez[] cachezs;
    @Override
    public Cachez getCachez() {
        return cachez;
    }

    @Override
    public void setCachez(Cachez cachez) {
        this.cachez = cachez;
    }
}
