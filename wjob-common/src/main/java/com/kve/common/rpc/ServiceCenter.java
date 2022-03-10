package com.kve.common.rpc;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RPC服务端
 * @author: hujing39
 * @date: 2022-03-07
 */

public class ServiceCenter implements RpcServer {
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final HashMap<String, Object> serviceRegistry = new HashMap<String, Object>();
    private static boolean isRunning = false;
    private static int port;
    private static String host;

    public ServiceCenter() {}

    public void set(String host, int port) {
        ServiceCenter.port = port;
        ServiceCenter.host = host;
    }

    @Override
    public void stop() {
        isRunning = false;
        executor.shutdown();
    }

    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(host, port));
        System.out.println("connect server");
        try {
            while (true) {
                //监听客户端的TCP连接 收到TCP连接后封装成Task 由线程池执行
                executor.execute(new ServiceTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }

    @Override
    public void register(Class serverInterface, Class impl) {
        String className = serverInterface.getName();
        //拿到spring管理的ApplicationContext中的类
        serviceRegistry.put(className, SpringContextHolder.getApplicationContext().getBean(impl));
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPort() {
        return port;
    }

    private static class ServiceTask implements Runnable {
        Socket client = null;

        public ServiceTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;

            try {
                //将客户端发送的反序列化成对象 反射调用服务实现者 获取执行结果
                input = new ObjectInputStream(client.getInputStream());
                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Object serviceClass = serviceRegistry.get(serviceName);
                System.out.println(serviceClass);
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + "not found");
                }

                Class<?> clzz = Class.forName(serviceName);
                Method method = clzz.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass, arguments); //执行方法

                //将执行结果反序列化 通过socket发送给客户端
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
