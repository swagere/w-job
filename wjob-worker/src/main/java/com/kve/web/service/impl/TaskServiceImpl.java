package com.kve.web.service.impl;

import com.kve.web.service.TaskService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public class TaskServiceImpl implements TaskService {
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    @Override
    public void resumeJob(String jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        String[] keyArray = jobKey.split("\\.");
        scheduler.resumeJob(JobKey.jobKey(keyArray[1], keyArray[0]));
        scheduler.start();
    }
}
