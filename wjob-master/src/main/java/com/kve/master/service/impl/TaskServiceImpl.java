package com.kve.master.service.impl;

import com.kve.master.bean.dto.TaskPageQueryDTO;
import com.kve.master.bean.enums.TaskStatusEnum;
import com.kve.master.bean.param.TaskPageParam;
import com.kve.master.bean.param.TaskParam;
import com.kve.master.bean.vo.TaskPageVO;
import com.kve.master.config.ApplicationContextHelper;
import com.kve.master.service.TaskService;
import com.kve.master.util.PageUtils;
import com.kve.master.util.ParamUtil;
import com.kve.master.mapper.TaskEntityMapper;
import com.kve.master.bean.TaskEntity;
import com.kve.master.util.QuartzScheduleUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: hujing39
 * @date: 2022-03-14
 */

@Service
public class TaskServiceImpl implements TaskService {
    private static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

//    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskEntityMapper taskEntityMapper;

    /**
     * 新增任务
     * @param taskParam
     * @throws SchedulerException
     */
    @Override
    public void saveTask(TaskParam taskParam) throws Exception{
//        reentrantLock.lock();
        try {
            //基本参数是否为空校验
            if (!checkParamAfterSaveOrUpdate(taskParam)) {
                throw new Exception("基本参数校验失败");
            }
            //时间表达式校验
            if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
                throw new Exception("时间表达式校验失败");
            }
            //任务存在性校验
            if (existTask(taskParam.getTriggerName(), taskParam.getTriggerGroup())) {
                throw new Exception("任务已经被创建，请使用不同的应用名称或任务名称");
            }

            TaskEntity taskEntity = buildTaskEntity(taskParam);

            //数据持久化
            taskEntity.setJobStatus(TaskStatusEnum.CREATE.getValue()); //创建态
            taskEntityMapper.addTask(taskEntity);

            log.info("TaskService>> saveTask end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
        } finally {
//            reentrantLock.unlock();
//            log.debug("new job, unlock >> addJob >> param={}", JSON.toJSONString(taskParam));
        }

    }

    @Override
    public void updateJob(TaskParam taskParam) throws Exception {
        //基本参数是否为空校验
        if (!checkParamAfterSaveOrUpdate(taskParam)) {
            throw new Exception("基本参数校验失败");
        }
        //时间表达式校验
        if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
            throw new Exception("时间表达式校验失败");
        }

        TaskEntity oldTaskEntity = taskEntityMapper.findById(taskParam.getJobId());
        if (oldTaskEntity == null) {
            throw new Exception("任务不存在");
        }

        if (oldTaskEntity.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new Exception("任务正在执行，无法直接修改");
        }

        TaskEntity taskEntity = buildTaskEntity(taskParam);

        //数据持久化
        taskEntity.setJobStatus(TaskStatusEnum.CREATE.getValue()); //创建态
        taskEntityMapper.updateById(taskEntity);

        log.info("TaskService>> updateTask end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
    }

    /**
     * 删除任务
     */
    @Override
    public void deleteJob(TaskParam taskParam) throws Exception {
        TaskEntity taskEntity = taskEntityMapper.findById(taskParam.getJobId());
        if (taskEntity == null) {
            throw new Exception("任务不存在，无法执行删除操作");
        }

        if (taskEntity.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new Exception("任务正在执行，无法直接删除");
        }

        taskEntityMapper.removeById(taskEntity.getId(), taskEntity.getUpdateBy(), taskEntity.getUpdateName());

        log.info("TaskService>> deleteTask end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
    }

    /**
     * 获取任务列表
     */
    @Override
    public TaskPageVO listPageTask(TaskPageParam taskPageParam) throws Exception {
        //构建查询参数
        TaskPageQueryDTO pageQueryDTO = TaskPageQueryDTO.builder()
                .limit(PageUtils.getStartRow(taskPageParam.getPage(), taskPageParam.getLimit()))
                .pageSize(PageUtils.getOffset(taskPageParam.getLimit()))
                .targetNameLike(taskPageParam.getTargetNameLike())
                .targetMethodLike(taskPageParam.getTargetMethodLike())
                .triggerGroup(taskPageParam.getTriggerGroup())
                .jobStatus(taskPageParam.getJobStatus())
                .build();

        List<TaskEntity> taskList = taskEntityMapper.listPageByCondition(pageQueryDTO);
        if (CollectionUtils.isEmpty(taskList) || taskList.size() <= 0) {
            return TaskPageVO.initDefault();
        }

        return new TaskPageVO(taskList.size(), taskList);
    }

    /**
     * 获取任务详情
     */
    @Override
    public TaskEntity getJobDetail(TaskParam taskParam) throws Exception {
        TaskEntity taskEntity = taskEntityMapper.findById(taskParam.getJobId());
        if (taskEntity == null) {
            throw new Exception("任务不存在");
        }

        return taskEntity;
    }

    /**
     * 启动任务
     */
    @Override
    public void startJob(TaskParam taskParam) throws Exception{
//        reentrantLock.lock();
        try {
            //查询任务是否存在
            TaskEntity taskEntity = taskEntityMapper.findById(taskParam.getJobId());

            if (taskEntity == null) {
                throw new Exception("任务不存在");
            }

            //校验任务是否已启动
            // TODO: 2022/5/2 任务已启动不能仅仅判断其trigger是否存在 
            if (!taskEntity.getJobStatus().equals(TaskStatusEnum.CREATE.getValue()) && isStart(taskEntity)) {
                throw new Exception("任务已启动");
            }

            //时间表达式校验
            if (!CronExpression.isValidExpression(taskEntity.getCronExpression())) {
                throw new Exception("时间表达式校验失败");
            }

            //类、方法存在校验
            checkBeanAndMethodExists(taskEntity.getTargetClass(), taskEntity.getTargetMethod(), taskEntity.getTargetArguments());

            QuartzScheduleUtil.startJob(taskEntity);

            //更新任务状态
            TaskEntity task = new TaskEntity();
            task.setId(taskEntity.getId());
            taskEntity.setJobStatus(TaskStatusEnum.RUNNING.getValue());
            taskEntityMapper.updateById(taskEntity);

            log.info("TaskService >> startJob end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
        } finally {
//            reentrantLock.unlock();
//            log.debug("启动任务job , 释放锁 >> startJob >> param={}", JSON.toJSONString(taskParam));
        }
    }

    /**
     * 暂停任务
     */
    @Override
    public void pauseJob(TaskParam taskParam) throws Exception {
        TaskEntity taskEntity = taskEntityMapper.findById(taskParam.getJobId());
        if (!existTask(taskEntity.getTriggerName(), taskEntity.getTriggerGroup())) {
            throw new Exception("任务不存在");
        }

        //判断任务状态 校验任务是否已结束
        if (taskEntity.getJobStatus().equals(TaskStatusEnum.FINISH_SUCCESS.getValue()) || taskEntity.getJobStatus().equals(TaskStatusEnum.FINISH_EXCEPTION.getValue())) {
            throw new Exception("任务执行结束，无法执行暂停操作");
        }

        QuartzScheduleUtil.pauseJob(taskEntity);

        //更新任务状态
        TaskEntity task = new TaskEntity();
        task.setId(taskEntity.getId());
        taskEntity.setJobStatus(TaskStatusEnum.PAUSE.getValue());
        taskEntityMapper.updateById(taskEntity);

        log.info("TaskService >> pauseJob end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
    }

    /**
     * 恢复任务
     */
    @Override
    public void resumeJob(TaskParam taskParam) throws Exception {
        TaskEntity taskEntity = taskEntityMapper.findById(taskParam.getJobId());
        if (!existTask(taskEntity.getTriggerName(), taskEntity.getTriggerGroup())) {
            throw new Exception("任务不存在");
        }

        //判断任务状态 校验任务处于暂停/未开始状态
        if (taskEntity.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new Exception("任务正在执行中，无法执行恢复操作");
        }

        QuartzScheduleUtil.resumeJob(taskEntity);

        //更新任务状态
        TaskEntity task = new TaskEntity();
        task.setId(taskEntity.getId());
        taskEntity.setJobStatus(TaskStatusEnum.RUNNING.getValue());
        taskEntityMapper.updateById(taskEntity);

        log.info("TaskService >> resumeJob end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
    }

    /**
     * 停止/中断任务
     */
    @Override
    public void stopJob(TaskParam taskParam) throws Exception {
        TaskEntity taskEntity = taskEntityMapper.findById(taskParam.getJobId());
        if (taskEntity == null) {
            throw new Exception("任务不存在");
        }

        QuartzScheduleUtil.stopJob(taskEntity);

        //更新任务状态
        TaskEntity task = new TaskEntity();
        task.setId(taskEntity.getId());
        taskEntity.setJobStatus(TaskStatusEnum.FINISH_EXCEPTION.getValue());
        taskEntityMapper.updateById(taskEntity);

        log.info("TaskService >> stopJob end; id:{},operate:{}", taskParam.getJobId(), taskParam.getOperateName());
    }


    /**
     * 构建TaskEntity任务参数
     */
    private TaskEntity buildTaskEntity(TaskParam taskParam) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTargetClass(taskParam.getTargetClass());
        taskEntity.setTargetMethod(taskParam.getTargetMethod());
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
     * 检查任务是否已经被创建
     * triggerName triggerGroup
     */
    private boolean existTask(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
        if (scheduler.getTrigger(triggerKey) != null) {
            return true;
        }

        int count = taskEntityMapper.countByTriggerDetail(triggerGroup, triggerName);
        if (count > 0) {
            return true;
        }
        return false;
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
