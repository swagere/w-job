package com.kve.master.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.kve.common.util.ParamUtil;
import com.kve.master.mapper.ScheduleLogMapper;
import com.kve.master.model.bean.OperateLog;
import com.kve.master.model.bean.TaskInfo;
import com.kve.master.model.base.BaseParam;
import com.kve.master.model.dto.TaskPageQueryDTO;
import com.kve.master.model.enums.OperateLogTypeEnum;
import com.kve.master.model.enums.TaskStatusEnum;
import com.kve.master.model.param.TaskPageParam;
import com.kve.master.model.param.TaskParam;
import com.kve.master.model.vo.TaskDetailVO;
import com.kve.master.model.vo.TaskPageVO;
import com.kve.common.config.ApplicationContextHelper;
import com.kve.master.config.exception.WJobException;
import com.kve.master.config.response.SysExceptionEnum;
import com.kve.master.mapper.OperateLogMapper;
import com.kve.master.service.TaskService;
import com.kve.master.util.*;
import com.kve.master.mapper.TaskInfoMapper;
import com.kve.master.util.CompareObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Autowired
    private ScheduleLogMapper scheduleLogMapper;

    /**
     * 新增任务
     * @param taskParam
     * @throws SchedulerException
     */
    @Override
    public void saveTask(TaskParam taskParam) throws Exception{
        if (!checkParamAfterSaveOrUpdate(taskParam)) {
            throw new WJobException(SysExceptionEnum.PARAM_ILLEGAL, "基本参数校验失败");
        }
        //时间表达式校验
        if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
            throw new WJobException(SysExceptionEnum.PARAM_CORN_ILLEGAL, "时间表达式校验失败");
        }
        //任务存在性校验
        if (existTask(taskParam.getTriggerName(), taskParam.getTriggerGroup())) {
            throw new WJobException(SysExceptionEnum.TASK_GROUP_THE_SAME_EXISTS);
        }

        TaskInfo taskInfo = buildTaskEntity(taskParam);

        //重复性校验
        if (existTask(taskParam.getTriggerName(), taskParam.getTriggerGroup())) {
            throw new WJobException(SysExceptionEnum.TASK_GROUP_THE_SAME_EXISTS);
        }

        //数据持久化
        taskInfo.setJobStatus(TaskStatusEnum.CREATE.getValue()); //创建态
        taskInfoMapper.addTask(taskInfo);

        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLog(taskInfo.getId(), OperateLogTypeEnum.CREATE, taskParam));


//        log.info("TaskService>> saveTask end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
    }

    @Override
    public void updateJob(TaskParam taskParam) throws Exception {
        //基本参数是否为空校验
        if (!checkParamAfterSaveOrUpdate(taskParam)) {
            throw new WJobException(SysExceptionEnum.PARAM_ILLEGAL, "基本参数校验失败");
        }
        //时间表达式校验
        if (!CronExpression.isValidExpression(taskParam.getCronExpression())) {
            throw new WJobException(SysExceptionEnum.PARAM_CORN_ILLEGAL, "时间表达式校验失败");
        }

        TaskInfo oldTaskInfo = taskInfoMapper.findById(taskParam.getId());
        if (oldTaskInfo == null) {
            throw new WJobException(SysExceptionEnum.TASK_NOT_EXISTS);
        }

        if (oldTaskInfo.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_IS_RUNNING);
        }

        TaskInfo taskInfo = buildTaskEntity(taskParam);
        taskInfo.setId(taskParam.getId());

        //重复性校验
        if (existTask(taskParam.getTriggerName(), taskParam.getTriggerGroup())) {
            throw new WJobException(SysExceptionEnum.TASK_GROUP_THE_SAME_EXISTS);
        }

        //数据持久化
        taskInfo.setJobStatus(TaskStatusEnum.CREATE.getValue()); //创建态
        taskInfoMapper.updateById(taskInfo);

        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLogAfterUpdate(taskParam.getId(), oldTaskInfo, taskParam));

        //        log.info("TaskService>> updateTask end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
    }

    /**
     * 删除任务
     */
    @Override
    public void deleteJob(TaskParam taskParam) throws Exception {
        TaskInfo taskInfo = taskInfoMapper.findById(taskParam.getId());
        if (taskInfo == null) {
            throw new WJobException(SysExceptionEnum.TASK_NOT_EXISTS);
        }

        if (taskInfo.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_IS_RUNNING);
        }

        taskInfoMapper.removeById(taskInfo.getId(), taskInfo.getUpdateBy(), taskInfo.getUpdateName());
        scheduleLogMapper.deleteByTriggerId(taskInfo.getId());


        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLog(taskParam.getId(), OperateLogTypeEnum.DELETE, taskParam));


//        log.info("TaskService>> deleteTask end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
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
                .targetClassLike(taskPageParam.getTargetClassLike())
                .targetMethodLike(taskPageParam.getTargetMethodLike())
                .triggerGroupLike(taskPageParam.getTriggerGroupLike())
                .triggerNameLike(taskPageParam.getTriggerNameLike())
                .jobStatus(taskPageParam.getJobStatus())
                .build();

        List<TaskInfo> taskList = taskInfoMapper.listPageByCondition(pageQueryDTO);
        if (CollectionUtils.isEmpty(taskList) || taskList.size() <= 0) {
            return TaskPageVO.initDefault();
        }

        return new TaskPageVO(taskList.size(), buildTaskDetailVOList(taskList));
    }

    /**
     * 获取任务详情
     */
    @Override
    public TaskInfo getJobDetail(TaskParam taskParam) throws Exception {
        TaskInfo taskInfo = taskInfoMapper.findById(taskParam.getId());
        if (taskInfo == null) {
            throw new Exception("任务不存在");
        }

        return taskInfo;
    }

    /**
     * 启动任务
     */
    @Override
    public void startJob(TaskParam taskParam) throws Exception{
        //查询任务是否存在
        TaskInfo taskInfo = taskInfoMapper.findById(taskParam.getId());

        if (taskInfo == null) {
            throw new WJobException(SysExceptionEnum.TASK_NOT_EXISTS);
        }

        //校验任务是否已启动
        // TODO: 2022/5/2 任务已启动不能仅仅判断其状态
        if (taskInfo.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_IS_RUNNING);
        }

        //时间表达式校验
        if (!CronExpression.isValidExpression(taskInfo.getCronExpression())) {
            throw new WJobException(SysExceptionEnum.PARAM_CORN_ILLEGAL);
        }

        //类、方法存在校验
//        checkBeanAndMethodExists(taskInfo.getTargetClass(), taskInfo.getTargetMethod(), taskInfo.getTargetArguments());

        QuartzScheduleUtil.startJob(taskInfo);

        //更新任务状态
        TaskInfo task = new TaskInfo();
        task.setId(taskInfo.getId());
        taskInfo.setJobStatus(TaskStatusEnum.RUNNING.getValue());
        taskInfoMapper.updateById(taskInfo);

        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLog(task.getId(), OperateLogTypeEnum.OPEN, taskParam));


//        log.info("TaskService >> startJob end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
    }

    /**
     * 暂停任务
     */
    @Override
    public void pauseJob(TaskParam taskParam) throws Exception {
        TaskInfo taskInfo = taskInfoMapper.findById(taskParam.getId());
        if (!existTask(taskInfo.getTriggerName(), taskInfo.getTriggerGroup())) {
            throw new WJobException(SysExceptionEnum.TASK_NOT_EXISTS);
        }

        //判断任务状态 校验任务是否已结束
        if (taskInfo.getJobStatus().equals(TaskStatusEnum.FINISH_SUCCESS.getValue()) || taskInfo.getJobStatus().equals(TaskStatusEnum.FINISH_EXCEPTION.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_FINISH);
        } else if (taskInfo.getJobStatus().equals(TaskStatusEnum.CREATE.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_CREATE);
        }

        QuartzScheduleUtil.pauseJob(taskInfo);

        //更新任务状态
        TaskInfo task = new TaskInfo();
        task.setId(taskInfo.getId());
        taskInfo.setJobStatus(TaskStatusEnum.PAUSE.getValue());
        taskInfoMapper.updateById(taskInfo);

        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLog(task.getId(), OperateLogTypeEnum.CLOSE, taskParam));


//        log.info("TaskService >> pauseJob end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
    }

    /**
     * 恢复任务
     */
    @Override
    public void resumeJob(TaskParam taskParam) throws Exception {
        TaskInfo taskInfo = taskInfoMapper.findById(taskParam.getId());
        if (!existTask(taskInfo.getTriggerName(), taskInfo.getTriggerGroup())) {
            throw new WJobException(SysExceptionEnum.TASK_NOT_EXISTS);
        }

        //判断任务状态 校验任务处于暂停/未开始状态
        if (taskInfo.getJobStatus().equals(TaskStatusEnum.RUNNING.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_IS_RUNNING);
        } else if (taskInfo.getJobStatus().equals(TaskStatusEnum.CREATE.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_CREATE);
        } else if (taskInfo.getJobStatus().equals(TaskStatusEnum.FINISH_EXCEPTION.getValue()) || taskInfo.getJobStatus().equals(TaskStatusEnum.FINISH_SUCCESS.getValue())) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_FINISH);
        }

        QuartzScheduleUtil.resumeJob(taskInfo);

        //更新任务状态
        TaskInfo task = new TaskInfo();
        task.setId(taskInfo.getId());
        taskInfo.setJobStatus(TaskStatusEnum.RUNNING.getValue());
        taskInfoMapper.updateById(taskInfo);

        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLog(task.getId(), OperateLogTypeEnum.CLOSE, taskParam));


//        log.info("TaskService >> resumeJob end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
    }

    /**
     * 停止/中断任务
     */
    @Override
    public void stopJob(TaskParam taskParam) throws Exception {
        TaskInfo taskInfo = taskInfoMapper.findById(taskParam.getId());
        if (taskInfo == null) {
            throw new WJobException(SysExceptionEnum.TASK_NOT_EXISTS);
        }

        QuartzScheduleUtil.stopJob(taskInfo);

        //更新任务状态
        TaskInfo task = new TaskInfo();
        task.setId(taskInfo.getId());
        taskInfo.setJobStatus(TaskStatusEnum.FINISH_EXCEPTION.getValue());
        taskInfoMapper.updateById(taskInfo);

        //异步添加操作日志
        CompletableFuture.runAsync(() -> this.addLog(task.getId(), OperateLogTypeEnum.CLOSE, taskParam));


//        log.info("TaskService >> stopJob end; id:{},operate:{}", taskParam.getId(), taskParam.getOperateName());
    }

    /**
     * 添加日志
     */
    private void addLog(Integer jobId, OperateLogTypeEnum operateLogTypeEnum, BaseParam operateBO) {
        try {
            TaskInfo scheduledQuartzJobInfo = taskInfoMapper.findById(jobId);
            if (null == scheduledQuartzJobInfo) {
                return;
            }
            this.doAddLog(jobId, operateLogTypeEnum, operateBO, JSON.toJSONString(scheduledQuartzJobInfo));
        } catch (Exception e) {
//            log.error("TaskServiceImpl >> aync addLog add log exception", e);
        }
    }

    /**
     * 更新任务-记录操作日志
     */
    private void addLogAfterUpdate(Integer jobId, TaskInfo oldJobInfo, BaseParam operateBO) {
        try {
            TaskInfo newJobInfo = taskInfoMapper.findById(jobId);
            if (null == newJobInfo) {
                return;
            }

            Map<String, Object> allFieldValues = CompareObjectUtil.getAllFieldValues(oldJobInfo, newJobInfo, TaskInfo.class);
            //记录操作日志
            this.doAddLog(jobId, OperateLogTypeEnum.UPDATE, operateBO, JSON.toJSONString(allFieldValues));
        } catch (Exception e) {
//            log.error("TaskServiceImpl >> aync addLog  add log exception", e);
        }
    }


    /**
     * 添加日志
     */
    private void doAddLog(Integer jobId, OperateLogTypeEnum operateLogTypeEnum, BaseParam baseParam, String content) {
        OperateLog jobLog = new OperateLog();
        jobLog.setJobId(jobId);
        jobLog.setLogType(operateLogTypeEnum.getType());
        jobLog.setLogDesc(operateLogTypeEnum.getDesc());
        jobLog.setContent(content == null ? "" : content);
        jobLog.setOperateId(baseParam.getOperateBy());
        jobLog.setOperateName(baseParam.getOperateName());
        String ipAddress = baseParam.getClientIp();
        if (!IpAddressUtil.LOCAL_IP.equals(ipAddress)) {
            String addressByIp = IpAddressUtil.getAddressByIp(ipAddress);
            if (!StringUtils.isEmpty(addressByIp)) {
                ipAddress = ipAddress + IpAddressUtil.SEPARATE + addressByIp;
            }
        }
        jobLog.setIpAddress(ipAddress);
        String remarks = baseParam.getBrowserName();
        if (!StringUtils.isEmpty(baseParam.getOs())) {
            remarks = remarks + IpAddressUtil.SEPARATE + baseParam.getOs();
        }
        jobLog.setRemarks(remarks);
        operateLogMapper.addLog(jobLog);
    }

    /**
     * 构建任务分页列表
     */
    private List<TaskDetailVO> buildTaskDetailVOList(List<TaskInfo> jobList) {
        TaskDetailVO quartzJobItem = null;
        List<TaskDetailVO> resultList = new ArrayList<>(jobList.size());
        for (TaskInfo jobInfo : jobList) {
            quartzJobItem = BeanCopyUtil.copy(jobInfo, TaskDetailVO.class);
            quartzJobItem.setLastRunTime("");
            if (jobInfo.getLastRunTimestamp() > 0) {
                quartzJobItem.setLastRunTime(DateUtil.formatDateTime(new Date(jobInfo.getLastRunTimestamp())));
            }
            resultList.add(quartzJobItem);
        }

        return resultList;
    }

    /**
     * 构建TaskEntity任务参数
     */
    private TaskInfo buildTaskEntity(TaskParam taskParam) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTargetClass(taskParam.getTargetClass());
        taskInfo.setTargetMethod(taskParam.getTargetMethod());
        taskInfo.setTriggerGroup(taskParam.getTriggerGroup());
        taskInfo.setTriggerName(taskParam.getTriggerName());
        taskInfo.setCronExpression(taskParam.getCronExpression());
        taskInfo.setDescription(taskParam.getDescription());
        taskInfo.setTargetArguments(taskParam.getTargetArguments());

        taskInfo.setCreateBy(taskParam.getOperateBy());
        taskInfo.setCreateName(taskParam.getOperateName());
        taskInfo.setUpdateBy(taskParam.getOperateBy());
        taskInfo.setUpdateName(taskParam.getOperateName());
        return taskInfo;
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
        if (scheduler.checkExists(triggerKey)) {
            return true;
        }

        int count = taskInfoMapper.countByTriggerDetail(triggerGroup, triggerName);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 校验任务类或-方法是否在环境中存在
     */
    public void checkBeanAndMethodExists(String targetClass, String targetMethod, String methodArgs) throws Exception{
        if (null == targetClass) {
            throw new WJobException(SysExceptionEnum.INVALID_PARAM);
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
                throw new WJobException(SysExceptionEnum.JOB_CLASS_METHOD_NOT_EXISTS);
            }
        } catch (Exception e) {
//            log.error("[ TaskService ] >> checkBeanAndMethodIsExists error ", e);
            throw e;
        }
    }

}
