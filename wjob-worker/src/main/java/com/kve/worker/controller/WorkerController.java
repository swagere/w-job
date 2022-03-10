package com.kve.worker.controller;

import com.alibaba.fastjson.JSON;
import com.kve.common.rpc.RpcServer;
import com.kve.common.rpc.ServiceCenter;
import com.kve.common.service.QuartzService;
import com.kve.worker.service.impl.QuartzServiceImpl;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: hujing39
 * @date: 2022-03-08
 */

@RestController
@RequestMapping("/worker")
public class WorkerController {
    @Autowired
    private QuartzService quartzService;

    @Autowired
    ServiceCenter serviceCenter;

    @RequestMapping("/connect")
    @ResponseBody
    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serviceCenter.set("localhost", 9000);
                    serviceCenter.register(QuartzService.class, QuartzServiceImpl.class);
                    serviceCenter.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @RequestMapping("/start")
    @ResponseBody
    public Boolean start(@RequestBody String str) {
        String jobKey = JSON.parseObject(str).get("jobKey").toString();
        try {
            quartzService.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
