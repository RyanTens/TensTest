package com.tens.rpc;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class RpcServer {
    public void start(int port, String clazz) {
        ServerSocket server = null;
        /*
        1.创建socket连接
        2.获取所有rpc服务类
        3.创建线程池
        4.获取客户端连接
        5.查找并执行服务
         */

        try {
            server = new ServerSocket(port);
            Map<String, Object> services = getService(clazz);
            Executor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            while (true) {
                Socket client = server.accept();
                RpcService sercvice = new RpcService(client, services);
                executor.execute(sercvice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private Map<String, Object> getService(String clazz) {
        try {
            HashMap<String, Object> services = new HashMap<>();
            String[] clazzes = clazz.split(",");
            ArrayList<Class<?>> classes = new ArrayList<>();
            for (String cl : clazzes) {
                List<Class<?>> classList = getClasses(cl);
                classes.addAll(classList);
            }

            for (Class<?> cla : classes) {
                Object obj = cla.newInstance();
//                services.put(cla.getAnnotation(com.tens.rpc.RpcServer.class).value().getName(), obj);
            }
            return services;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Class<?>> getClasses(String pkgName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader");
            }
            String path = pkgName.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (directory.exists()) {
            String[] files = directory.list();
            File[] fileList = directory.listFiles();
            for (int i = 0; fileList != null && i < fileList.length; i++) {
                File file = fileList[i];
                if (file.isFile() && file.getName().endsWith(".class")) {
                    Class<?> clazz = Class.forName(pkgName + "." + files[i].substring(0, files[i].length() - 6));
//                    if (clazz.getAnnotation(com.tens.rpc.RpcServer.class) != null) {
//                        classes.add(clazz);
//                    }
                } else if (file.isDirectory()) {
                    List<Class<?>> result = getClasses(pkgName + "." + file.getName());
                    if (result != null && result.size() != 0) {
                        classes.addAll(result);
                    }
                }
            }
        } else {
            throw new ClassNotFoundException(pkgName + "doesn't appear to be valid package");
        }
        return classes;
    }
}
