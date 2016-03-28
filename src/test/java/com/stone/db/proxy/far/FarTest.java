package com.stone.db.proxy.far;

import com.stone.db.proxy.service.impl.UserService;
import com.stone.db.proxy.service.interfaces.IUserService;

/**
 * Created by Stony on 2016/3/14.
 */
public class FarTest {

    public static void main(String[] args){
        String host = "localhost";
        int port = 9001;
        FarExport.server(port);
        FarExport.register(IUserService.class.getName(),UserService.class);
        for (int i = 0; i < 10; i++){
            IUserService userService = (IUserService) FarImport.invoker(IUserService.class,host,port);
            userService.task("wash face.");
        }
    }
}
