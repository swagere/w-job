package com.kve.web.server;

import com.kve.rpc.RpcServer;
import com.kve.rpc.ServiceCenter;
import com.kve.web.service.TaskService;
import com.kve.web.service.impl.TaskServiceImpl;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */


public class StartServer {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RpcServer serviceServer = new ServiceCenter("localhost", 8088);
                    serviceServer.register(TaskService.class, TaskServiceImpl.class);
                    serviceServer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
