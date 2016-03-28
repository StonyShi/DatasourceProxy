package com.stone.db.proxy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
