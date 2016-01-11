package com.stone.db.proxy.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ShiHui on 2016/1/10.
 */
public class User implements Serializable {
    private Integer id;
    private String name;
    private Date birthday;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
