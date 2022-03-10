package com.kve.worker.service.impl;

import com.kve.common.service.QuartzService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */
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
