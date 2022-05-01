package com.kve.master.service;

import com.alibaba.fastjson.JSON;
import com.kve.common.bean.HelloJob;
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

    /**
     * 新增任务
     * @param taskParam
     * @throws SchedulerException
     */
    public void saveTask(TaskParam taskParam) throws Exception{
        reentrantLock.lock();
        try {
            //基本参数是否为空校验
            if (!checkParamAfterSaveOrUpdate(taskParam)) {
                throw new Exception("基本参数校验失败");
            }
            //时间表达式校验
            if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
                throw new Exception("时间表达式校验失败");
            }

            TaskEntity taskEntity = buildTaskEntity(taskParam);

            //数据持久化
            taskEntity.setJobStatus(1); //创建态
            taskEntityMapper.addTask(taskEntity);
            log.info("TaskService>> saveTask end  id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
        } finally {
            reentrantLock.unlock();
            log.debug("new job, unlock >> addJob >> param={}", JSON.toJSONString(taskParam));
        }

    }

//    public void pauseJob(TaskParam taskParam) throws SchedulerException {
//        // TODO: 2022/3/14 数据库更改TaskInfo status设置为2
//
//        JobKey jobKey = new JobKey(taskParam.getJobName(), taskParam.getJobGroup());
//        scheduler.pauseJob(jobKey);
//    }

//    public void resumeJob(TaskParam taskParam) throws SchedulerException {
//        // TODO: 2022/3/15 数据库更改TaskInfo status设置为3
//
//        // TODO: 2022/3/15 RPC调用 远程开启任务
//        JobKey jobKey = new JobKey(taskParam.getJobName(), taskParam.getJobGroup());
//        scheduler.resumeJob(jobKey);
//    }

    /**
     * 启动任务
     */
    public void startJob(TaskParam taskParam) throws Exception{
        reentrantLock.lock();
        try {
            //查询任务是否存在
            TaskEntity taskEntity = taskEntityMapper.findByAppNameAndId(taskParam.getJobId(), appName);

            //校验任务是否已启动
            if (!taskEntity.getJobStatus().equals(1) && isStart(taskEntity)) {
                throw new Exception("任务已启动");
            }

            //时间表达式校验
            if (!CronExpression.isValidExpression(taskEntity.getCronExpression())) {
                throw new Exception("时间表达式校验失败");
            }

            //类、方法存在校验
            checkBeanAndMethodExists(taskEntity.getTargetClass(), taskEntity.getTargetMethod(), taskEntity.getTargetArguments());

            //RPC调用
            // TODO: 2022/3/16  worker 利用quartz的方法创建job时的数据一致性
            RpcService.startJob(taskEntity);

            //更新任务状态
            TaskEntity task = new TaskEntity();
            task.setId(taskEntity.getId());
            task.setAppName(appName);
            taskEntity.setJobStatus(2);
            taskEntityMapper.updateByAppNameAndId(taskEntity);

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
        taskEntity.setAppName(appName);
        taskEntity.setTargetClass(taskParam.getTargetClass());
        taskEntity.setTargetClass(taskParam.getTargetClass());
        taskEntity.setTriggerGroup(taskParam.getTriggerGroup());
        taskEntity.setTriggerName(taskParam.getTriggerName());
        taskEntity.setCronExpression(taskParam.getCronExpression());
        taskEntity.setDescription(taskParam.getDescription());
        taskEntity.setTargetArguments(taskParam.getTargetArguments());

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
        if (StringUtils.isEmpty(taskParam.getTargetClass())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getTargetMethod())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getTriggerGroup())) {
            return false;
        }
        if (StringUtils.isEmpty(taskParam.getTriggerName())) {
            return false;
        }

        return true;
    }

    /**
     * 任务是否已经启动
     */
    public boolean isStart(TaskEntity taskEntity) {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskEntity.getTriggerName(),
                taskEntity.getTriggerGroup());
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            return (null != trigger);
        } catch (Exception e) {
            log.info("[ TaskService ] >> isStart exception triggerName:{},triggerGroup:{}", taskEntity.getTriggerName(),
                    taskEntity.getTriggerGroup(), e);
            return false;
        }
    }

    /**
     * 校验任务类或-方法是否在环境中存在
     */
    public void checkBeanAndMethodExists(String targetClass, String targetMethod, String methodArgs) throws Exception{
        if (null == targetClass) {
            throw new Exception("类名为空");
        }
        try {
            Object jobClassInfo = ApplicationContextHelper.getApplicationContext().getBean(targetClass);
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
