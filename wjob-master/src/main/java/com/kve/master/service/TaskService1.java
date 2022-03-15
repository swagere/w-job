package com.kve.master.service;

import com.kve.master.model.Task1;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.StringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author: hujing39
 * @date: 2022-03-08
 */

@Service
public class TaskService1 {
    @Autowired
    private Scheduler scheduler;

    public void deleteJob(String name, String group) throws Exception {
        try {
            scheduler.deleteJob(JobKey.jobKey(name, group));
            scheduler.start();
        } catch (SchedulerException e) {
            throw e;
        }

    }

    /**
     * scheduleType:
     * 1：simpleSchedule
     * 2：cronSchedule
     * @param taskParam
     * @throws Exception
     */
    public void createJob(Task1 taskParam) throws Exception {
        System.out.println(scheduler);
        Trigger trigger = null;
        switch (taskParam.getTriggerType()) {
            case "1":
                Integer rate = taskParam.getRate();
                Integer times = taskParam.getTimes();
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(taskParam.getJobName(), taskParam.getJobGroup())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(rate)
                                .withRepeatCount(times))
                        .build();
                break;
            case "2":
                String cronExpression = String.format("%s %s %s %s %s %s",
                        taskParam.getSecond(), taskParam.getMinute(), taskParam.getHour(), taskParam.getDay(), taskParam.getMouth(), taskParam.getWeek());
                boolean isValid = CronExpression.isValidExpression(cronExpression);
                if (!isValid) {
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(taskParam.getJobName(), taskParam.getJobGroup())
                            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                            .build();
                }
                break;
            default:
                throw new IOException();
        }
        try {
            Class jobClass = Class.forName(taskParam.getJobClassName());
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity(taskParam.getJobName(), taskParam.getJobGroup())
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<HashMap<String, Object>> list() throws Exception{
        List<HashMap<String, Object>> jobList = new ArrayList<HashMap<String, Object>>();
        try {
            List<String> groups = scheduler.getJobGroupNames();
            for (String group : groups) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(new GroupMatcher(group, StringMatcher.StringOperatorName.EQUALS){});
                for (JobKey jobKey : jobKeys) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    HashMap<String, Object> jobInfoMap = new HashMap<String, Object>();
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    jobInfoMap.put("triggers", triggers);
                    jobInfoMap.put("jobDetail", jobDetail);
                    jobList.add(jobInfoMap);
                }
            }
        } catch (SchedulerException e) {
            throw e;
        }
        return jobList;
    }

}
