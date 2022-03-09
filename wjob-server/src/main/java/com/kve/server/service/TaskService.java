package com.kve.server.service;

import com.kve.server.model.TaskInfo;
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
public class TaskService {
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
     * @param taskInfo
     * @throws Exception
     */
    public void createJob(TaskInfo taskInfo) throws Exception {
        Trigger trigger = null;
        switch (taskInfo.getTriggerType()) {
            case "1":
                Integer rate = taskInfo.getRate();
                Integer times = taskInfo.getTimes();
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(taskInfo.getJobName(), taskInfo.getJobGroup())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(rate)
                                .withRepeatCount(times))
                        .build();
                break;
            case "2":
                String cronExpression = String.format("%s %s %s %s %s %s",
                        taskInfo.getSecond(), taskInfo.getMinute(), taskInfo.getHour(), taskInfo.getDay(), taskInfo.getMouth(), taskInfo.getWeek());
                boolean isValid = CronExpression.isValidExpression(cronExpression);
                if (!isValid) {
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(taskInfo.getJobName(), taskInfo.getJobGroup())
                            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                            .build();
                }
                break;
            default:
                throw new IOException();
        }
        try {
            Class jobClass = Class.forName(taskInfo.getJobClassName());
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity(taskInfo.getJobName(), taskInfo.getJobGroup())
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
