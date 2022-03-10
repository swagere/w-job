package com.kve.rpcServer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RPC服务端
 * 集成spring
 * @author: hujing39
 * @date: 2022-03-07
 */
@Component
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final HashMap<String, Object> serviceRegistry = new HashMap<String, Object>();

    private static int port = 9999;
    private static String host = "localhost";

    public RpcServer(){}

    /**
     * 启动服务
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket server = null;
                try {
                    server = new ServerSocket();
                    server.bind(new InetSocketAddress(host, port));
                    System.out.println("connect server : " + host + ":" + port);
                    while (true) {
                        //监听客户端的TCP连接 收到TCP连接后封装成Task 由线程池执行
                        executor.execute(new ServiceTask(server.accept()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (server != null) {
                        try {
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();

    }

    /**
     * 利用容器处理自定义的RPC注解
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcServiceAnno.class);
        if (beansWithAnnotation != null) {
            for (Object bean : beansWithAnnotation.values()) {
                // 处理注解
                RpcServiceAnno rpcServiceAnno = bean.getClass().getAnnotation(RpcServiceAnno.class);
                String serviceName = rpcServiceAnno.value().getName();
                System.out.println("IOC publish service ---> "+serviceName);
                serviceRegistry.put(serviceName, bean);
            }
        }
    }

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
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + " not found");
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
