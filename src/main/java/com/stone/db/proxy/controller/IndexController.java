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



    @RequestMapping(value = {"/index,/"})
    public String index(ModelMap modelMap){
        modelMap.put("title","Index Page.");
        return "index";
    }


}
