package com.kve.master.model;

import com.kve.common.model.ActionEnum;
import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import com.kve.common.config.ApplicationContextHelper;
import com.kve.master.callback.CallBackServer;
import com.kve.master.mapper.ScheduleLogMapper;
import com.kve.master.mapper.TaskInfoMapper;
import com.kve.master.model.bean.ScheduleLog;
import com.kve.master.model.bean.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 定时任务 核心Bean
 */
//@DisallowConcurrentExecution
@Slf4j
public class TaskBean implements Job {
    private static TaskInfoMapper taskInfoMapper;
    private static CallBackServer callBackServer;
    private static ScheduleLogMapper scheduleLogMapper;

    static {
        taskInfoMapper = ApplicationContextHelper.getBean(TaskInfoMapper.class);
        callBackServer = ApplicationContextHelper.getBean(CallBackServer.class);
        scheduleLogMapper = ApplicationContextHelper.getBean(ScheduleLogMapper.class);
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap map = context.getMergedJobDataMap();
        //任务ID
        Integer triggerId = (Integer) map.get("triggerId");
        //目标类名
        String targetClass = map.getString("targetClass");
        //目标方法
        String targetMethod = map.getString("targetMethod");
        //方法参数
        String targetArguments = map.getString("targetArguments");

        //调度日志处理
        ScheduleLog scheduleLog = new ScheduleLog();
        scheduleLog.setTriggerId(triggerId);
        scheduleLogMapper.save(scheduleLog);

        //远程调用参数构造
        RequestModel requestModel = new RequestModel();
        requestModel.setTargetClass(targetClass);
        requestModel.setTargetArguments(targetArguments);
        requestModel.setTargetMethod(targetMethod);
        requestModel.setAction(ActionEnum.RUN.getValue());
        requestModel.setTimestamp(System.currentTimeMillis());
        requestModel.setMasterAddress(callBackServer.getAddress());
        requestModel.setScheduleLogId(scheduleLog.getId());

        //本机地址
        List<String> addressList = new ArrayList<>();
        addressList.add("127.0.0.1:9999");

        //调用执行器，开始任务执行
        log.info("[ TaskBean ] run a task, request:{}", requestModel);

        ResponseModel responseModel = executeRun(addressList, requestModel, scheduleLog);

        log.info("[ TaskBean ] run a task, response:{}", responseModel);

        //调度日志更新
        scheduleLog.setTriggerTime(new Date());
        scheduleLog.setTriggerStatus(responseModel.getStatus());
        scheduleLog.setTriggerMsg(responseModel.getMsg());
        scheduleLogMapper.updateById(scheduleLog);

        //更新最后执行时间
        updateAfterRun(triggerId);
    }

    private ResponseModel executeRun(List<String> addressList, RequestModel requestModel, ScheduleLog scheduleLog) {
        if (addressList==null || addressList.size() < 1) {
            ResponseModel result = new ResponseModel();
            result.setStatus(ResponseModel.FAIL);
            result.setMsg( "executor execute error, [address] is null" );
            return result;
        } else if (addressList.size() == 1) {
            String address = addressList.get(0);

            scheduleLog.setExecutorAddress(address);
            ResponseModel triggerCallback = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(address), requestModel);
            String failoverMessage = MessageFormat.format("executor running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2}.<br><hr>", address, triggerCallback.getStatus(), triggerCallback.getMsg());
            triggerCallback.setMsg(failoverMessage);
            return triggerCallback;
        } else {
            //对执行器随机排序，然后按照顺序取对执行器发出心跳检测请求
            // 第一个检测为存活状态的执行器将会被选定并发送调度请求
            Collections.shuffle(addressList);

            String failoverMessage = "";
            for (String address : addressList) {
                if (!StringUtils.isEmpty(address)) {
                    //心跳检测
                    RequestModel beatRequest = new RequestModel();
                    beatRequest.setTimestamp(System.currentTimeMillis());
                    beatRequest.setAction(ActionEnum.BEAT.getValue());
                    ResponseModel beatResponse = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(address), beatRequest);
                    failoverMessage += MessageFormat.format("BEAT running, <br>>>>[address] : {0}, <br>>>>[status] : {1}, <br>>>>[msg] : {2}.<br><hr>", address, beatResponse.getStatus(), beatRequest.getMsg());

                    if (beatResponse.getStatus().equals(ResponseModel.SUCCESS)) {
                        scheduleLog.setExecutorAddress(address);

                        ResponseModel triggerCallback = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(address), requestModel);
                        failoverMessage += MessageFormat.format("Trigger running, <br>>>>[address] : {0}, <br>>>>[status]:{1}, <br>>>>[msg] : {2}.<br><hr>", address, triggerCallback.getStatus(), triggerCallback.getMsg());
                        triggerCallback.setMsg(failoverMessage);
                        return triggerCallback;
                    }
                }
            }

            //没有成功的执行器
            ResponseModel result = new ResponseModel();
            result.setStatus(ResponseModel.FAIL);
            result.setMsg(failoverMessage);
            return result;
        }
    }

    private void updateAfterRun(Integer triggerId) {
        try {
            //更新最后执行时间
            TaskInfo task = new TaskInfo();
            task.setId(triggerId);
            task.setLastRunTimestamp(System.currentTimeMillis());
            taskInfoMapper.updateById(task);
        } catch (Exception e) {
            log.error("[ TaskBean ] >> job updateAfterRun exception; TaskId:{}", triggerId, e);
        }
    }
}
