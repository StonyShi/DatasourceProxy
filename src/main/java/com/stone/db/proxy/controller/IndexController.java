package com.stone.db.proxy.controller;

import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.UserService;
import com.stone.db.proxy.support.MailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @Resource(name = "mailTemplate")
    private MailTemplate mailSender;


    public void register(User user) {

        //TODO Do the registration logic...

        sendConfirmationEmail(user);
    }
    private void sendConfirmationEmail(final User user) {
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("user",user);
        model.put("to",user.getName());
        mailSender.send(model,"mails/register");
    }
}
