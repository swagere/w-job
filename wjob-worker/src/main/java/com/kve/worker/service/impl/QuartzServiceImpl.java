package com.kve.worker.service.impl;

import com.kve.common.bean.TaskBean;
import com.kve.common.bean.TaskEntity;
import com.kve.common.service.QuartzService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */
@Service
public class QuartzServiceImpl implements QuartzService {
    private static Logger log = LoggerFactory.getLogger(QuartzServiceImpl.class);

    @Autowired
    private Scheduler scheduler;

    @Override
    public void startJob(TaskEntity taskEntity) throws Exception{
        JobDataMap dataMap = getJobDataMap(taskEntity);
        try {
            //Trigger-----
            TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //若任务调度存在
            if (trigger != null) {
                // TODO: 2022/5/2 重新执行任务的条件不能仅仅依靠trigger是否存在判断
                trigger.getTriggerBuilder().withIdentity(taskEntity.getTriggerName(), taskEntity.getTriggerGroup()).withSchedule(
                                CronScheduleBuilder.cronSchedule(taskEntity.getCronExpression()))
                        .build();
                //重新执行任务
                scheduler.rescheduleJob(triggerKey, trigger);
                log.info("[ QuartzService ] >> startJob exist task end triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(), taskEntity.getTriggerGroup());
                return;
            }

            //任务调度类不存在
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(taskEntity.getCronExpression()))
                    .build();

            //Job-----
            JobKey jobKey = JobKey.jobKey(taskEntity.getTargetMethod(), taskEntity.getTargetClass());
            JobDetail job = scheduler.getJobDetail(jobKey);
            //任务调度实体不存在
            if (job == null) {
                job = JobBuilder.newJob(TaskBean.class)
                        // 任务名（类名+方法名+参数）任务组
                        .withIdentity(jobKey)
                        .usingJobData(dataMap)
                        .build();
            }

            //执行任务
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.info("[ QuartzService ] >> startJob exception triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            throw new Exception("任务启动失败");
        }

        log.info("[ QuartzService ] >> enable new task end triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                taskEntity.getTriggerGroup());
    }

    /**
     * 构建JobDataMap
     * @return
     */
    private JobDataMap getJobDataMap(TaskEntity taskEntity) {
        JobDataMap dataMap = new JobDataMap();
        if (!StringUtils.isEmpty(taskEntity.getId())) dataMap.put("jobId", taskEntity.getId());
        if (!StringUtils.isEmpty(taskEntity.getTargetClass())) dataMap.put("targetClass", taskEntity.getTargetClass());
        if (!StringUtils.isEmpty(taskEntity.getTargetMethod())) dataMap.put("targetMethod", taskEntity.getTargetMethod());
        if (!StringUtils.isEmpty(taskEntity.getTargetArguments())) dataMap.put("targetArguments", taskEntity.getTargetArguments());
        return dataMap;
    }

}
