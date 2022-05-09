package com.kve.master.model;

import com.kve.common.model.ActionEnum;
import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import com.kve.common.config.ApplicationContextHelper;
import com.kve.master.callback.CallBackServer;
import com.kve.master.mapper.TaskInfoMapper;
import com.kve.master.model.bean.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 定时任务 核心Bean
 */
//@DisallowConcurrentExecution
@Slf4j
public class TaskBean implements Job {
    private static TaskInfoMapper taskInfoMapper;
    private static CallBackServer callBackServer;

    static {
        taskInfoMapper = ApplicationContextHelper.getApplicationContext().getBean(TaskInfoMapper.class);
        callBackServer = ApplicationContextHelper.getApplicationContext().getBean(CallBackServer.class);
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

        RequestModel requestModel = new RequestModel();
        requestModel.setTargetClass(targetClass);
        requestModel.setTargetArguments(targetArguments);
        requestModel.setTargetMethod(targetMethod);
        requestModel.setAction(ActionEnum.RUN.getValue());
        requestModel.setTimestamp(System.currentTimeMillis());
        requestModel.setMasterAddress(callBackServer.getAddress());

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(triggerId);

        //本机地址
        List<String> addressList = new ArrayList<>();
        addressList.add("127.0.0.1:9999");

        //调用执行器，开始任务执行
        log.info("[ TaskBean ] run a task, request:{}", requestModel);

        ResponseModel responseModel = executeRun(addressList, requestModel, taskInfo);

        log.info("[ TaskBean ] run a task, response:{}", responseModel);

        //更新最后执行时间
        updateAfterRun(taskInfo);
    }

    private ResponseModel executeRun(List<String> addressList, RequestModel requestModel, TaskInfo taskInfo) {
        if (addressList==null || addressList.size() < 1) {
            ResponseModel result = new ResponseModel();
            result.setStatus(ResponseModel.FAIL);
            result.setMsg( "executor execute error, [address] is null" );
            return result;
        } else if (addressList.size() == 1) {
            String address = addressList.get(0);

            taskInfo.setExecutorAddress(address);
            ResponseModel triggerCallback = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(address), requestModel);
            String failoverMessage = MessageFormat.format("executor running, [address] : {0}, [status] : {1}, [msg] : {2}", address, triggerCallback.getStatus(), triggerCallback.getMsg());
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
                    failoverMessage += MessageFormat.format("BEAT running, [address]:{0}, [status]:{1}, [msg]:{2}.", address, beatResponse.getStatus(), beatRequest.getMsg());

                    if (beatResponse.getStatus().equals(ResponseModel.SUCCESS)) {
                        taskInfo.setExecutorAddress(address);
                        ResponseModel triggerCallback = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(address), requestModel);
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

    private void updateAfterRun(TaskInfo taskInfo) {
        try {
            //更新最后执行时间
            TaskInfo task = new TaskInfo();
            task.setId(taskInfo.getId());
            task.setExecutorAddress(taskInfo.getExecutorAddress());
            task.setLastRunTimestamp(System.currentTimeMillis());
            taskInfoMapper.updateById(task);
        } catch (Exception e) {
            log.error("[ TaskBean ] >> job updateAfterRun exception; TaskId:{}", taskInfo.getId(), e);
        }
    }
}
