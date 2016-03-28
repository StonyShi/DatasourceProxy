package com.stone.db.proxy.service.impl;

import com.stone.db.proxy.cache.Cachezable;
import com.stone.db.proxy.dao.UserDao;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by ShiHui on 2016/1/10.
 */
@Service("userService")
public class UserService implements IUserService{

    @Autowired
    private UserDao userDao;

    public int addUser(User user){
        return userDao.addUser(user);
    }
    public int saveUser(User user){
        return userDao.addUser(user);
    }

    @Cachezable(key = "getUsers", suffixType = Cachezable.SuffixType.DATE_NAME)
    public List<User> getUsers(){
        return userDao.getUsers();
    }

    /**
     * prefixType  'getUser_' + #id
     * @see Cachezable.PrefixType#METHOD_NAME
     * @param id
     * @return
     */
    @Cachezable(key = "#id", prefixType = Cachezable.PrefixType.METHOD_NAME)
    public User getUser(Integer id) {
        return userDao.getUser(id);
    }

    public void tx(){
        throw new RuntimeException("tx rollback.");
    }


    public List<User> getUsersAnd(User user){
        userDao.addUser(user);
        return userDao.getUsers();
    }

    @Override
    public void register(User user) {

    }

    @Override
    public void task(String name) {
        System.out.println("Task " + name +" ----------------------------------------------------------");
    }
}
