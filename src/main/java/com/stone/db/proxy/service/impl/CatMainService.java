package com.stone.db.proxy.service.impl;

import com.stone.db.proxy.mapper.CatMapper;
import com.stone.db.proxy.model.Cat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>Created with IntelliJ IDEA. </p>
 * <p>User: Stony </p>
 * <p>Date: 2016/8/9 </p>
 * <p>Time: 16:06 </p>
 * <p>Version: 1.0 </p>
 */
@Service
public class CatMainService {

    @javax.annotation.Resource
    CatService catService;

    @Resource
    CatMapper catMapper;


    public Cat queryCat(int id){
        return catService.getCat(id);
    }

    public int insertCat(Cat cat){
        return catService.saveCat(cat);
    }

    public void insertAndQuery(Cat cat){
        catService.saveCat(cat);

        catService.getCat(2);
    }

    public void queryAndInsert(Cat cat){
        catService.getCat(2);

        catService.saveCat(cat);
    }

    public void findCat(){
        Cat cat = new Cat();
        cat.setName("Test_"+System.currentTimeMillis());
        cat.setCreateDate(new Date());
        catMapper.insert(cat);
    }
}
