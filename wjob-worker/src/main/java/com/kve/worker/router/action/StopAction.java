package com.kve.worker.router.action;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.worker.router.HandlerRouter;
import com.kve.worker.router.thread.TaskThread;

public class StopAction implements ExecutorAction{
    @Override
    public ResponseModel execute(RequestModel requestModel) {
        String triggerKey = "TriggerId:" + requestModel.getTriggerId();
        TaskThread taskThread = HandlerRouter.loadJobThread(triggerKey);

        if (taskThread == null) {
            return new ResponseModel(ResponseModel.FAIL, "任务未运行，无法停止");
        }

        taskThread.stopTask();
        return new ResponseModel(ResponseModel.SUCCESS, null);
    }
}