package com.stone.db.proxy.far;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by Stony on 2016/3/14.
 */
public class FarImport {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static Object invoker(final Class clazz, final String host, final int port){
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Future future = executor.submit(new FarCall(host,port,method,args,clazz));
                return future.get(3000,TimeUnit.SECONDS);
            }
        });
    }
    static class FarCall implements Callable{
        String host;
        int port;
        Method method;
        Object[] args;
        Class clazz;

        public FarCall(String host, int port, Method method, Object[] args, Class clazz) {
            this.host = host;
            this.port = port;
            this.method = method;
            this.args = args;
            this.clazz = clazz;
        }

        @Override
        public Object call() throws Exception {
            try {
                Socket socket = new Socket(host,port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeUTF(clazz.getName());
                out.writeUTF(method.getName());
                out.writeObject(args);
                Object result = in.readObject();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        }
    }
}
