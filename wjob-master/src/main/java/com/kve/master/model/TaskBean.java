package com.kve.master.model;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import com.kve.common.config.ApplicationContextHelper;
import com.kve.master.mapper.TaskInfoMapper;
import com.kve.master.model.bean.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务 核心Bean
 */
//@DisallowConcurrentExecution
@Slf4j
public class TaskBean implements Job {

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
        requestModel.setAction("RUN");
        requestModel.setTimestamp(System.currentTimeMillis());

        //执行器地址
        List<String> addressList = new ArrayList<>();
        addressList.add("127.0.0.1:9999");

        //调用执行器，开始任务执行
        ResponseModel responseModel = executeRemote(addressList, requestModel);

        //更新最后执行时间
        updateAfterRun(triggerId);
    }

    private ResponseModel executeRemote(List<String> addressList, RequestModel requestModel) {
        if (addressList==null || addressList.size() < 1) {
            ResponseModel result = new ResponseModel();
            result.setStatus(ResponseModel.FAIL);
            result.setMsg( "executor execute error, [address] is null" );
            return result;
        } else if (addressList.size() == 1) {
            String address = addressList.get(0);

            ResponseModel triggerCallback = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(address), requestModel);
            String failoverMessage = MessageFormat.format("executor running, [address] : {0}, [status] : {1}, [msg] : {2}", address, triggerCallback.getStatus(), triggerCallback.getMsg());
            triggerCallback.setMsg(failoverMessage);
            return triggerCallback;
        } else {

        }

        return null;
    }

    private void updateAfterRun(Integer jobId) {
        try {
            TaskInfoMapper taskInfoMapper = ApplicationContextHelper.getApplicationContext().getBean(TaskInfoMapper.class);
            //更新最后执行时间
            TaskInfo task = new TaskInfo();
            task.setId(jobId);
            task.setLastRunTimestamp(System.currentTimeMillis());
            taskInfoMapper.updateById(task);
        } catch (Exception e) {
            log.error("[ JobDetail ] >> job updateAfterRun exception jobId:{} , projectKey:{} ", jobId, e);
        }
    }
}
