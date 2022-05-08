package com.kve.worker.router.action;

import com.kve.worker.router.HandlerRouter;
import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.worker.router.thread.TaskThread;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunAction implements ExecutorAction {
    @Override
    public ResponseModel execute(RequestModel requestModel) {
        String triggerKey = "TriggerId:" + requestModel.getTriggerId();
        TaskThread taskThread = HandlerRouter.loadJobThread(triggerKey);

        if (taskThread == null) {
            taskThread = HandlerRouter.registerTaskThread(triggerKey);
        }
        taskThread.pushTriggerQueue(requestModel);
        return new ResponseModel(ResponseModel.SUCCESS, null);
    }

}
