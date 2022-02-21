package com.kve.quartz_example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.concurrent.TimeUnit;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

  public class Quartz_multiTrigger {
      public static void main(String[] args) {

          try {
              //从Factory中获取Scheduler实例
              Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

              scheduler.start();

              // 定义job
              JobDetail job= JobBuilder.newJob(HelloJob.class)
                      .withIdentity("job1", "group1")
                      .build();

              // 定义trigger
              Trigger trigger= TriggerBuilder.newTrigger()
                      .withIdentity("trigger1", "group1")
                      .startNow()
                      .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                              .withIntervalInSeconds(5)
                              .repeatForever())
                      .build();

              Trigger trigger1= TriggerBuilder.newTrigger()
                      .withIdentity("trigger2", "group1")
                      .forJob("job1", "group1")
                      .startNow()
                      .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                              .withIntervalInSeconds(5)
                              .repeatForever())
                      .build();

              // 将job和trigger注入scheduler
              scheduler.scheduleJob(job, trigger);
              scheduler.scheduleJob(trigger1);

              TimeUnit.SECONDS.sleep(20);

              scheduler.shutdown();

          } catch (SchedulerException se) {
              se.printStackTrace();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  }