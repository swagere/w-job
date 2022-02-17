package com.kve.quartz_example;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * @author: hujing39
 * @date: 2022-02-16
 */

@Slf4j
public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("HelloJob.execute" + " " + Thread.currentThread().getName());
    }
}
