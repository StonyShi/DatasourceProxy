package com.stone.db.proxy.far;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Stony on 2016/3/14.
 */
public class FarExport<T> {

    public static ExecutorService executorService = Executors.newFixedThreadPool(30);
    public static Map<String,Class> register = new HashMap<String,Class>();
    private static AtomicBoolean serverRunning = new AtomicBoolean(false);
    public static void server(final int port){
        Runnable serverRun = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(port);
                    setServerRunning(true);
                    try {
                        while(isServerRunning()){
                            Socket socket = server.accept();
                            new FarExportWork(socket).start();
                        }
                    } finally {
                        server.close();
                    }
                } catch (Exception e) {
                    setServerRunning(false);
                    e.printStackTrace();
                }
            }
        };
        executorService.execute(serverRun);
        System.out.println("serverRun..................");

    }
    public static boolean isServerRunning(){
        return serverRunning.get();
    }
    public static void setServerRunning(boolean status){
        serverRunning.set(status);
    }
    public static void register(String key,Class value){
        register.put(key,value);
    }
    public static void register(Class value){
        Class[] interfaces = value.getInterfaces();
        for(Class inf : interfaces){
            register(inf.getName(), value);
        }
    }
    public static void register(Object value){
        register(value.getClass());
    }
    public static Class getObject(String key){
        return register.get(key);
    }
    public static Object getRegisterInstance(String key){
        try {
            return getObject(key).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    static class FarExportWork implements Runnable{
        private Socket socket;

        public FarExportWork(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                ObjectInputStream in = null;
                ObjectOutputStream out = null;
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());
                    String className = in.readUTF();
                    String methodName = in.readUTF();
                    Object[] args = (Object[]) in.readObject();
                    Class[] parameterTypes = new Class[args.length];
                    for (int i = 0; i < args.length; i++) {
                        Object arg = args[i];
                        parameterTypes[i] = arg.getClass();
                    }
                    Object service = FarExport.getRegisterInstance(className);
                    if(service == null){
                        out.writeObject(new NullPointerException("The Service["+className+"] is null"));
                        out.flush();
                        return;
                    }
                    Method method = service.getClass().getMethod(methodName,parameterTypes);
                    Object result = method.invoke(service,args);
                    out.writeObject(result);
                    out.flush();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void start() {
            executorService.execute(this);
            System.out.println("新增连接：" + socket.getInetAddress() + ":" + socket.getPort());
        }
    }

}
