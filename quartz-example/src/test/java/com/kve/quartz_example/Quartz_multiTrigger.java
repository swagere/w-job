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
              StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
              Scheduler scheduler = stdSchedulerFactory.getScheduler();
//              Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler(); 官方示例

              scheduler.start();

              // 定义job
              JobDetail job= JobBuilder.newJob(HelloJob.class)
                      .withIdentity("job1", "group1")
                      .storeDurably() //设置job持久化（当没有trigger与job直接关联时，job默认不保存
                      .build();

              // 定义trigger
              Trigger trigger= TriggerBuilder.newTrigger()
                      .withIdentity("trigger1", "group1")
                      .forJob("job1", "group1")
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
              scheduler.addJob(job, false); // job只能注入一次
              scheduler.scheduleJob(trigger);
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