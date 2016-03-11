package com.stone.db.proxy.service.interfaces;

import com.stone.db.proxy.annotation.Hessian;
import com.stone.db.proxy.model.User;

import java.util.List;

// Context.API_V2 用于客户端调用
@Hessian(context = Hessian.Context.API_V2, uri = "/v2/userService")
public interface IUserService {
     
    public List<User> getUsers();

    public User getUser(Integer id);

    public int addUser(User user);

    public int saveUser(User user);

    public List<User> getUsersAnd(User user);

    public void register(User user);
}