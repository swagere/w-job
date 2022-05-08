package com.kve.worker_example.rpc;//package com.kve.worker.rpc;
//
//import com.kve.worker.jetty.ExecutorHandler;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RpcSpringConfig implements InitializingBean {
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ServiceCenter serviceCenter = new ServiceCenter();
//                    serviceCenter.set("localhost", 9000);
//                    serviceCenter.register(RpcHandler.class, ExecutorHandler.class);
//                    serviceCenter.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//}
