package com.kve.master.service;

import com.alibaba.fastjson.JSON;
import com.kve.common.config.ApplicationContextHelper;
import com.kve.common.config.CommonConfigConstants;
import com.kve.common.service.QuartzService;
import com.kve.common.util.ParamUtil;
import com.kve.common.mapper.TaskEntityMapper;
import com.kve.common.bean.TaskEntity;
import com.kve.common.bean.TaskParam;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: hujing39
 * @date: 2022-03-14
 */

@Service
public class TaskService {
    private static Logger log = LoggerFactory.getLogger(TaskService.class);

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Value("${wjob.appName}")
    private static final String appName = CommonConfigConstants.appName;


    @Autowired
    private Scheduler scheduler;
    @Autowired
    private TaskEntityMapper taskEntityMapper;
    @Autowired
    private QuartzService quartzService;

    /**
     * 新增任务
     * @param taskParam
     * @throws SchedulerException
     */
    public void saveTask(TaskParam taskParam) throws Exception{
        taskParam.setAppName(appName);

        reentrantLock.lock();
        try {
            //基本参数是否为空校验
            if (this.checkParamAfterSaveOrUpdate(taskParam)) {
                throw new Exception("基本参数校验失败");
            }
            //时间表达式校验
            if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
                throw new Exception("时间表达式校验失败");
            }
            //判重(任务组和任务名称相同的)
            int count = taskEntityMapper.countByJobDetail(taskParam.getAppName(), taskParam.getJobGroup(), taskParam.getJobClass(), taskParam.getJobMethod());
            if (count > 0) {
                throw new Exception("任务已存在或重复添加");
            }

            TaskEntity taskEntity = buildTaskEntity(taskParam);

            //数据持久化
            taskParam.setJobStatus(1); //未开始状态
            taskEntityMapper.addTask(taskEntity);
            log.info("TaskService>> saveTask end  id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
        } finally {
            reentrantLock.unlock();
            log.debug("new job, unlock >> addJob >> param={}", JSON.toJSONString(taskParam));
        }

    }

    public void pauseJob(TaskParam taskParam) throws SchedulerException {
        // TODO: 2022/3/14 数据库更改TaskInfo status设置为2

        JobKey jobKey = new JobKey(taskParam.getJobName(), taskParam.getJobGroup());
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(TaskParam taskParam) throws SchedulerException {
        // TODO: 2022/3/15 数据库更改TaskInfo status设置为3

        // TODO: 2022/3/15 RPC调用 远程开启任务
        JobKey jobKey = new JobKey(taskParam.getJobName(), taskParam.getJobGroup());
        scheduler.resumeJob(jobKey);
    }

    /**
     * 启动任务
     */
    public void startJob(TaskParam taskParam) throws Exception{
        reentrantLock.lock();
        try {
            //查询任务是否存在
            TaskEntity taskEntity = taskEntityMapper.findByAppNameAndId(taskParam.getJobId(), appName);

            //校验任务是否已启动
            if (!taskParam.getJobStatus().equals(1) && isStart(taskEntity)) {
                throw new Exception("任务已启动");
            }

            //时间表达式校验
            if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
                throw new Exception("时间表达式校验失败");
            }

            //类、方法存在校验
            checkBeanAndMethodExists(taskParam.getJobClass(), taskParam.getJobMethod(), taskParam.getMethodArgs());

            //RPC调用
            quartzService.startJob(taskEntity);

            //更新任务状态
            taskEntityMapper.updateByAppNameAndId(taskEntity.getId(), appName);

            log.info("TaskService >> startJob end  id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
        } finally {
            reentrantLock.unlock();
            log.debug("启动任务job , 释放锁 >> startJob >> param={}", JSON.toJSONString(taskParam));
        }
    }


    /**
     * 构建TaskEntity任务参数
     */
    private TaskEntity buildTaskEntity(TaskParam taskParam) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setAppName(taskParam.getAppName());
        taskEntity.setJobClass(taskParam.getJobClass());
        taskEntity.setJobMethod(taskParam.getJobMethod());
        taskEntity.setJobGroup(taskParam.getJobGroup());
        taskEntity.setJobName(taskParam.getJobName());
        taskEntity.setCronExpression(taskParam.getCronExpression());
        taskEntity.setDescription(taskParam.getDescription());
        taskEntity.setJobArguments(taskParam.getMethodArgs());

//        taskEntity.setCreateBy(jobSaveBO.getOperateBy());
//        taskEntity.setCreateName(jobSaveBO.getOperateName());
//        taskEntity.setUpdateBy(jobSaveBO.getOperateBy());
//        taskEntity.setUpdateName(jobSaveBO.getOperateName());
        return taskEntity;
    }

    /**
     * 检查参数是否为空
     */
    private Boolean checkParamAfterSaveOrUpdate(TaskParam taskParam) {
        if (StringUtils.isEmpty(taskParam.getCronExpression())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getJobClass())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getJobMethod())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getJobGroup())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getJobName())) {
            return false;
        }

        return true;
    }

    /**
     * 任务是否已经启动
     */
    public boolean isStart(TaskEntity taskEntity) {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                taskEntity.getJobGroup());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            return (null != trigger);
        } catch (Exception e) {
            log.info("[ TaskService ] >> isStart exception triggerName:{},JobGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getJobGroup(), e);
            return false;
        }
    }

    /**
     * 校验任务类或-方法是否在环境中存在
     */
    public void checkBeanAndMethodExists(String jobClass, String targetMethod, String methodArgs) throws Exception{
        if (null == jobClass) {
            throw new Exception("类名为空");
        }
        try {
            Object jobClassInfo = ApplicationContextHelper.getApplicationContext().getBean(jobClass);
            //任务参数
            Object[] jobArs = ParamUtil.getJobArgs(methodArgs);
            Class jobClazz = jobClassInfo.getClass();
            Class[] parameterType = ParamUtil.getParameters(jobArs);
            //执行任务方法
            Method method = jobClazz.getDeclaredMethod(targetMethod, parameterType);
            if (null == method) {
                throw new Exception("方法为空");
            }
        } catch (Exception e) {
            log.error("[ TaskService ] >> checkBeanAndMethodIsExists error ", e);
            throw e;
        }
    }
}
