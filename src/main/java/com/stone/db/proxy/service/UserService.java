package com.stone.db.proxy.service;

import com.stone.db.proxy.cache.CacheableMap;
import com.stone.db.proxy.dao.UserDao;
import com.stone.db.proxy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by ShiHui on 2016/1/10.
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public int addUser(User user){
        return userDao.addUser(user);
    }
    public int saveUser(User user){
        return userDao.addUser(user);
    }

    @CacheableMap(key = "getUsers", suffixType = CacheableMap.SuffixType.DATE_NAME)
    public List<User> getUsers(){
        return userDao.getUsers();
    }

    /**
     * prefixType  'getUser_' + #id
     * @see com.stone.db.proxy.cache.CacheableMap.PrefixType#METHOD_NAME
     * @param id
     * @return
     */
    @CacheableMap(key = "#id", prefixType = CacheableMap.PrefixType.METHOD_NAME)
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
}
