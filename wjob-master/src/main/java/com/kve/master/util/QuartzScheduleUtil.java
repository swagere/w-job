package com.kve.master.util;

import com.kve.master.model.TaskBean;
import com.kve.master.model.bean.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class QuartzScheduleUtil{
    private static Scheduler scheduler;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        QuartzScheduleUtil.scheduler = scheduler;
    }

    public static void startJob(TaskInfo taskInfo) throws Exception{
        JobDataMap dataMap = getJobDataMap(taskInfo);

        try {
            //Job-----
            JobKey jobKey = JobKey.jobKey(taskInfo.getTargetMethod(), taskInfo.getTargetClass());
            log.info("scheduler:" +  jobKey);
            JobDetail job = scheduler.getJobDetail(jobKey);
            //任务调度实体不存在
            if (job == null) {
                job = JobBuilder.newJob(TaskBean.class)
                        .withIdentity(jobKey)
                        .storeDurably(true) //任务执行结束后 数据库数据不随着trigger删除而删除
                        .build();
                scheduler.addJob(job, false);
            }

            //Trigger-----
            TriggerKey triggerKey = TriggerKey.triggerKey(taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //若任务调度存在
            if (trigger != null) {
                // TODO: 2022/5/3 是否有必要改成抛出异常：任务已经开始执行；是否需要校验任务状态
                trigger.getTriggerBuilder().withIdentity(taskInfo.getTriggerName(), taskInfo.getTriggerGroup()).withSchedule(
                                CronScheduleBuilder.cronSchedule(taskInfo.getCronExpression()))
                        .build();
                //刷新任务
                scheduler.rescheduleJob(triggerKey, trigger);
                log.info("[ QuartzScheduleUtil ] >> resumeJob exist task end; triggerName:{},triggerGroup:{}", taskInfo.getTriggerName(), taskInfo.getTriggerGroup());
                return;
            }

            //任务调度类不存在
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(job) //将job与trigger绑定
                    .usingJobData(dataMap)
                    .withSchedule(CronScheduleBuilder.cronSchedule(taskInfo.getCronExpression()))
                    .build();

            //执行任务
            scheduler.scheduleJob(trigger);

            log.info("[ QuartzScheduleUtil ] >> start new task end; triggerName:{},triggerGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzScheduleUtil ] >> startJob exception; triggerName:{},triggerGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            throw e;
        }
    }

    public static void pauseJob(TaskInfo taskInfo) throws Exception {
        try {
            //远程调用执行器
//            RequestModel requestModel = new RequestModel();
//            requestModel.setTimestamp(System.currentTimeMillis());
//            requestModel.setAction(ActionEnum.PAUSE.getValue());
//            requestModel.setTriggerId(taskInfo.getId());
//
//            ResponseModel responseModel = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(taskInfo.getExecutorAddress()), requestModel);
//
//            if (responseModel.getStatus().equals(ResponseModel.FAIL)) {
//                log.error("[ QuartzScheduleUtil ] >> pause a task exception; msg:{}", responseModel.getMsg());
//                throw new WJobException(SysExceptionEnum.FAIL_SCHEDULER, responseModel.getMsg());
//            }

            TriggerKey triggerKey = TriggerKey.triggerKey(taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            scheduler.pauseTrigger(triggerKey);

            log.info("[ QuartzScheduleUtil ] >> pause a task end; triggerName:{},triggerGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzSchedulerUtil ] >> pause a task exception; triggerName:{},JobGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            throw e;
        }

    }

    public static void resumeJob(TaskInfo taskInfo) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            scheduler.resumeTrigger(triggerKey);
            log.info("[ QuartzScheduleUtil ] >> resume a task end; triggerName:{},triggerGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzSchedulerUtil ] >> resume a task exception; triggerName:{},JobGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            throw e;
        }
    }

    public static void stopJob(TaskInfo taskInfo) throws Exception {
        try {
            //远程调用执行器
//            RequestModel requestModel = new RequestModel();
//            requestModel.setTimestamp(System.currentTimeMillis());
//            requestModel.setAction(ActionEnum.STOP.getValue());
//            requestModel.setTriggerId(taskInfo.getId());
//
//            ResponseModel responseModel = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(taskInfo.getExecutorAddress()), requestModel);
//
//            if (responseModel.getStatus().equals(ResponseModel.FAIL)) {
//                log.error("[ QuartzScheduleUtil ] >> stop a task exception; msg:{}", responseModel.getMsg());
//                throw new WJobException(SysExceptionEnum.FAIL_SCHEDULER, responseModel.getMsg());
//            }

            TriggerKey triggerKey = TriggerKey.triggerKey(taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);

            log.info("[ QuartzScheduleUtil ] >> stop a task end; triggerName:{},triggerGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzSchedulerUtil ] >> stop a task exception; triggerName:{},JobGroup:{}", taskInfo.getTriggerName(),
                    taskInfo.getTriggerGroup());
            throw e;
        }

    }

    /**
     * 构建JobDataMap
     * @return
     */
    private static JobDataMap getJobDataMap(TaskInfo taskInfo) {
        JobDataMap dataMap = new JobDataMap();
        if (!StringUtils.isEmpty(taskInfo.getId())) dataMap.put("triggerId", taskInfo.getId());
        if (!StringUtils.isEmpty(taskInfo.getTargetClass())) dataMap.put("targetClass", taskInfo.getTargetClass());
        if (!StringUtils.isEmpty(taskInfo.getTargetMethod())) dataMap.put("targetMethod", taskInfo.getTargetMethod());
        if (!StringUtils.isEmpty(taskInfo.getTargetArguments())) dataMap.put("targetArguments", taskInfo.getTargetArguments());
        return dataMap;
    }

}
