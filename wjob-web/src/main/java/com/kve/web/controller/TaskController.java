package com.kve.web.controller;

import com.alibaba.fastjson.JSON;
import com.kve.rpc.RpcClient;
import com.kve.web.model.TaskInfo;
import com.kve.web.service.TaskService;
import com.kve.web.util.PropertyRead;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.StringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private SchedulerFactory schedulerFactory;
    private static List<TaskService> serviceList = new ArrayList<TaskService>();

    static {
        //初始化
        String hostList = PropertyRead.getKey("hostList");
        String[] hosts = hostList.split(",");
        for (String host : hosts) {
            TaskService service = RpcClient.getRemoteProxyObj(TaskService.class,
                    new InetSocketAddress(host, 8088));
            serviceList.add(service);
        }
    }


    @RequestMapping("/deleteJob")
    @ResponseBody
    private Boolean deleteJob(@RequestBody String str) {
//        String queryGroup = JSON.parseObject(str).get("queryGroup").toString();
//        String queryJobName = JSON.parseObject(str).get("queryJobName").toString();
//        if (!StringUtils.isEmpty(queryGroup)) {
//            try {
//                queryGroup = URLEncoder.encode(queryGroup, "UTF-8");
//            }catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        if (!StringUtils.isEmpty(queryJobName)) {
//            try {
//                queryJobName = URLEncoder.encode(queryJobName, "UTF-8");
//            }catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
        String jobKey = JSON.parseObject(str).get("jobKey").toString();
        String[] keyArray = jobKey.split("\\.");

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.deleteJob(JobKey.jobKey(keyArray[1], keyArray[0]));
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @RequestMapping("list")
    @ResponseBody
    public Boolean toList() {
        List<HashMap<String, Object>> jobList = new ArrayList<HashMap<String, Object>>();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
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
            e.printStackTrace();
            return false;
        }

        System.out.println(jobList);
        return true;
    }

    @RequestMapping("toAddJob")
    @ResponseBody
    public Boolean toAddJob() {
        List<String> jobGroups = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            jobGroups = scheduler.getJobGroupNames();
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println(jobGroups);
        return true;
    }

    /**
     * scheduleType:
     * 1 simpleSchedule
     * 2 cronSchedule
     * @param taskInfo
     * @return
     */
    @RequestMapping("addJob")
    @ResponseBody
    public Boolean addJob(@RequestBody TaskInfo taskInfo) {
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
                return false;
        }
        try {
            Class jobClass = Class.forName(taskInfo.getJobClassName());
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity(taskInfo.getJobName(), taskInfo.getJobGroup())
                    .build();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @RequestMapping("resumeJob")
    @ResponseBody
    private Boolean resumeJob(@RequestBody String str){
        String jobKey = JSON.parseObject(str).get("jobKey").toString();

        try {
            for (TaskService service : serviceList) {
                service.resumeJob(jobKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
