package com.kve.worker.config;

import com.kve.common.rpc.ServiceCenter;
import com.kve.common.service.QuartzService;
import com.kve.worker.service.impl.QuartzServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author: hujing39
 * @date: 2022-03-10
 */

@Component
public class SpringConfig implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServiceCenter serviceCenter = new ServiceCenter();
                    serviceCenter.set("localhost", 9000);
                    serviceCenter.register(QuartzService.class, QuartzServiceImpl.class);
                    serviceCenter.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
