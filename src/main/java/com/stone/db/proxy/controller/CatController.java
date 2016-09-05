package com.stone.db.proxy.controller;

import com.stone.db.proxy.controller.context.AsyncControllerContext;
import com.stone.db.proxy.controller.context.CanceledCallable;
import com.stone.db.proxy.service.interfaces.ICatService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Stony on 2016/3/14.
 */
@Controller
@RequestMapping("/cat")
public class CatController {

    @Resource
    private AsyncControllerContext asyncControllerContext;

    @Resource
    private ICatService catService;

    @RequestMapping("/{uid}")
    public void getCat(HttpServletRequest request, final @PathVariable("uid") int uid){
        asyncControllerContext.submit(request, new CanceledCallable(request.getAsyncContext()) {
            @Override
            public Object call() throws Exception {
                return catService.getCat(uid);
            }
        });
    }
}
