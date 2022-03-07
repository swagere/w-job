package com.kve.web.task;

import org.quartz.*;

import java.util.Date;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */


public class HelloJob implements Job {
    private static int count;

    public HelloJob() {

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("goods job1 execute -------------------------------");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        count += 1;
        System.out.println("count == " + count);
        System.out.println("goods job1 execute end at ! - " + new Date());
//        if (count == 10) {
//            Scheduler scheduler = context.getScheduler();
//            JobKey jobKey = context.getTrigger().getJobKey();
//            try {
//                scheduler.pauseJob(jobKey);
//            } catch (SchedulerException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
