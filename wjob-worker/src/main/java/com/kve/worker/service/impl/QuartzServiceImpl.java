package com.kve.worker.service.impl;

import com.kve.common.service.QuartzService;
import com.kve.rpcServer.RpcServiceAnno;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */
@RpcServiceAnno(QuartzService.class)
@Service
public class QuartzServiceImpl implements QuartzService {
    @Autowired
    private Scheduler scheduler;

    @Override
    public void resumeJob(String jobKey) {
        try {
            System.out.println("执行任务, jobKey : " + jobKey);
            System.out.println("schedule : " + scheduler);
            System.out.println("example : " + this.hashCode());
            String[] keyArray = jobKey.split("\\.");
            JobKey key = JobKey.jobKey(keyArray[1], keyArray[0]);
            scheduler.resumeJob(key);
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
