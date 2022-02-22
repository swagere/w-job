package com.kve.quartz_example.method_1;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author: hujing39
 * @date: 2022-02-22
 */

public class JobBean extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println(context.getTrigger() + " " + Thread.currentThread().getName());
    }
}
