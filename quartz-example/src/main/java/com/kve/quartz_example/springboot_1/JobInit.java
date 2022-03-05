package com.kve.quartz_example.springboot_1;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * springboot 方式一
 * @author: hujing39
 * @date: 2022-02-22
 */

//@Component
public class JobInit {
    @Autowired
    public Scheduler scheduler;

    @PostConstruct
    public void initJob() throws SchedulerException {
        // 定义job
        JobDetail job= JobBuilder.newJob(JobBean.class)
                .build();

        // 定义trigger
        Trigger trigger= TriggerBuilder.newTrigger()
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(5))
                .startNow()
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
