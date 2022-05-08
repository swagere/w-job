package com.kve.worker_example.rpc;//package com.kve.worker.rpc;
//
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
///**
// * RPC客户端
// */
//
//public class RpcClient<T> {
//    public static<T> T getRemoteProxyObj(final Class<?> serviceInterface, final InetSocketAddress address) {
//        //将本地接口调用转换成JDK的动态代理 在动态代理中实现接口的远程调用
//        return (T) Proxy.newProxyInstance(
//                serviceInterface.getClassLoader(),
//                new Class<?>[]{serviceInterface},
//                new InvocationHandler() {
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                    Socket socket = null;
//                    ObjectOutputStream output = null;
//                    ObjectInputStream input = null;
//                    try {
//                        //创建socket客户端 连接远程服务提供者
//                        socket = new Socket();
//                        socket.connect(address);
//
//                        //将远程服务调用所需要的接口类、方法名、参数列表等编码后发送给服务调用者
//                        output = new ObjectOutputStream(socket.getOutputStream());
//                        output.writeUTF(serviceInterface.getName());
//                        output.writeUTF(method.getName());
//                        output.writeObject(method.getParameterTypes());
//                        output.writeObject(args);
//
//                        //同步等待服务器返回应答，获取应答后返回
//                        input = new ObjectInputStream(socket.getInputStream());
//                        return input.readObject();
//                    } finally {
//                        if (socket != null) {
//                            socket.close();
//                        }
//                        if (output != null) {
//                            output.close();
//                        }
//                        if (input != null) {
//                            input.close();
//                        }
//                    }
//                }
//
//            });
//    }
//}
