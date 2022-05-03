package com.kve.master.util;

import com.kve.master.bean.TaskBean;
import com.kve.master.bean.TaskEntity;
import com.kve.master.config.ApplicationContextHelper;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public class QuartzScheduleUtil{
    private static Logger log = LoggerFactory.getLogger(QuartzScheduleUtil.class);
    private static Scheduler scheduler;

    QuartzScheduleUtil() {
        this.scheduler = ApplicationContextHelper.getBean("Scheduler");
    }

    public static void startJob(TaskEntity taskEntity) throws Exception{
        JobDataMap dataMap = getJobDataMap(taskEntity);

        try {
            //Job-----
            JobKey jobKey = JobKey.jobKey(taskEntity.getTargetMethod(), taskEntity.getTargetClass());
            JobDetail job = scheduler.getJobDetail(jobKey);
            //任务调度实体不存在
            if (job == null) {
                job = JobBuilder.newJob(TaskBean.class)
                        .withIdentity(jobKey)
                        .storeDurably() //任务执行结束后 保存到数据库
                        .build();
                scheduler.addJob(job, false);
            }

            //Trigger-----
            TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //若任务调度存在
            if (trigger != null) {
                // TODO: 2022/5/3 是否有必要改成抛出异常：任务已经开始执行；是否需要校验任务状态
                trigger.getTriggerBuilder().withIdentity(taskEntity.getTriggerName(), taskEntity.getTriggerGroup()).withSchedule(
                                CronScheduleBuilder.cronSchedule(taskEntity.getCronExpression()))
                        .build();
                //刷新任务
                scheduler.rescheduleJob(triggerKey, trigger);
                log.info("[ QuartzScheduleUtil ] >> resumeJob exist task end; triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(), taskEntity.getTriggerGroup());
                return;
            }

            //任务调度类不存在
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(job) //将job与trigger绑定
                    .usingJobData(dataMap)
                    .withSchedule(CronScheduleBuilder.cronSchedule(taskEntity.getCronExpression()))
                    .build();

            //执行任务
            scheduler.scheduleJob(trigger);

            log.info("[ QuartzScheduleUtil ] >> start new task end; triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzScheduleUtil ] >> startJob exception; triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            throw new Exception("任务启动失败");
        }
    }

    public static void pauseJob(TaskEntity taskEntity) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
            scheduler.pauseTrigger(triggerKey);
            log.info("[ QuartzScheduleUtil ] >> pause a task end; triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzSchedulerUtil ] >> pause a task exception; triggerName:{},JobGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            throw new Exception("任务启动失败");
        }

    }

    public static void resumeJob(TaskEntity taskEntity) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
            scheduler.resumeTrigger(triggerKey);
            log.info("[ QuartzScheduleUtil ] >> resume a task end; triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzSchedulerUtil ] >> resume a task exception; triggerName:{},JobGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            throw new Exception("任务启动失败");
        }
    }

    public static void stopJob(TaskEntity taskEntity) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            log.info("[ QuartzScheduleUtil ] >> stop a task end; triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
        } catch (SchedulerException e) {
            log.info("[ QuartzSchedulerUtil ] >> stop a task exception; triggerName:{},JobGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            throw new Exception("任务启动失败");
        }

    }

    /**
     * 构建JobDataMap
     * @return
     */
    private static JobDataMap getJobDataMap(TaskEntity taskEntity) {
        JobDataMap dataMap = new JobDataMap();
        if (!StringUtils.isEmpty(taskEntity.getId())) dataMap.put("triggerId", taskEntity.getId());
        if (!StringUtils.isEmpty(taskEntity.getTargetClass())) dataMap.put("targetClass", taskEntity.getTargetClass());
        if (!StringUtils.isEmpty(taskEntity.getTargetMethod())) dataMap.put("targetMethod", taskEntity.getTargetMethod());
        if (!StringUtils.isEmpty(taskEntity.getTargetArguments())) dataMap.put("targetArguments", taskEntity.getTargetArguments());
        return dataMap;
    }

}
