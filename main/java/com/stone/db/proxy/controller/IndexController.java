package com.stone.db.proxy.controller;

import com.stone.db.proxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by ShiHui on 2016/1/9.
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/index,/"})
    public String index(ModelMap modelMap){
        modelMap.put("title","Index Page.");
        return "index";
    }

    @RequestMapping(value = "users")
    public String users(ModelMap modelMap){
        modelMap.put("users",userService.getUsers());
        return "users";
    }
    @RequestMapping(value = "user/{id}")
    public String users(@PathVariable Integer id, ModelMap modelMap){
        modelMap.put("users",userService.getUser(id));
        return "user";
    }
}
