package com.stone.db.proxy.mapper;

import com.stone.db.proxy.model.Cat;

public interface CatMapper {
    public int deleteByPrimaryKey(Integer id);

    public int insert(Cat record);

    public int insertSelective(Cat record);

    public Cat getCatById(Integer id);

    public int updateByPrimaryKeySelective(Cat record);

    public int updateCatById(Cat record);
}