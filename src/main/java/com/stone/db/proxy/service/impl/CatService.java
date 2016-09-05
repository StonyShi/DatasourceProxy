package com.stone.db.proxy.service.impl;

import com.stone.db.proxy.mapper.CatMapper;
import com.stone.db.proxy.model.Cat;
import com.stone.db.proxy.service.interfaces.ICatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * Created by Stony on 2016/3/14.
 */
@Service
public class CatService implements ICatService {

    @Resource
    CatMapper catMapper;

    @Override
    public Cat getCat(Integer uid) {
        return catMapper.getCatById(uid);
    }

    @Override
    public int saveCat(Cat cat) {
        return catMapper.insert(cat);
    }



}
