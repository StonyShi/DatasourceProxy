package com.stone.db.proxy.controller;

import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stony on 2016/3/9.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //@Resource(name = "mailTemplate")
    //private MailTemplate mailTemplate;

    @RequestMapping(value = "/list")
    public String users(ModelMap modelMap){
        modelMap.put("users",userService.getUsers());
        return "users";
    }
    @RequestMapping(value = "/{id}")
    public String users(@PathVariable Integer id, ModelMap modelMap){
        modelMap.put("user",userService.getUser(id));
        return "user";
    }

    @RequestMapping(value = "/register")
    public void register(User user) {
        //TODO Do the registration logic...
        sendConfirmationEmail(user);
    }
    private void sendConfirmationEmail(final User user) {
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("user",user);
        model.put("to",user.getName());
        //mailTemplate.send(model,"mails/register");
    }

    @ResponseBody
    @RequestMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object userData(){
        return userService.getUsers();
    }

}
