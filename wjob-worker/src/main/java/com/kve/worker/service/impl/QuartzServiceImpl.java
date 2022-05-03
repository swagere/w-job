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
                // TODO: 2022/5/2 恢复任务条件的不能仅仅依靠trigger是否存在判断
                trigger.getTriggerBuilder().withIdentity(taskEntity.getTriggerName(), taskEntity.getTriggerGroup()).withSchedule(
                                CronScheduleBuilder.cronSchedule(taskEntity.getCronExpression()))
                        .build();
                //重新执行任务
                scheduler.rescheduleJob(triggerKey, trigger);
                log.info("[ QuartzService ] >> resumeJob exist task end triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(), taskEntity.getTriggerGroup());
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
        } catch (SchedulerException e) {
            log.info("[ QuartzService ] >> startJob exception triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            throw new Exception("任务启动失败");
        }

        log.info("[ QuartzService ] >> start new task end triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                taskEntity.getTriggerGroup());
    }

    @Override
    public void pauseJob(TaskEntity taskEntity) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                taskEntity.getTriggerGroup());
        scheduler.pauseTrigger(triggerKey);
        log.info("[ QuartzService ] >> pause a task end triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                taskEntity.getTriggerGroup());
    }

    /**
     * 构建JobDataMap
     * @return
     */
    private JobDataMap getJobDataMap(TaskEntity taskEntity) {
        JobDataMap dataMap = new JobDataMap();
        if (!StringUtils.isEmpty(taskEntity.getId())) dataMap.put("triggerId", taskEntity.getId());
        if (!StringUtils.isEmpty(taskEntity.getTargetClass())) dataMap.put("targetClass", taskEntity.getTargetClass());
        if (!StringUtils.isEmpty(taskEntity.getTargetMethod())) dataMap.put("targetMethod", taskEntity.getTargetMethod());
        if (!StringUtils.isEmpty(taskEntity.getTargetArguments())) dataMap.put("targetArguments", taskEntity.getTargetArguments());
        return dataMap;
    }

}
