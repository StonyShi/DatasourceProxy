package com.stone.db.proxy.service.interfaces;

import com.stone.db.proxy.model.Cat;

import java.util.concurrent.Future;

/**
 * Created by Stony on 2016/3/14.
 */
public interface ICatService {


    public Cat getCat(Integer uid);

    public int saveCat(Cat cat);
}
