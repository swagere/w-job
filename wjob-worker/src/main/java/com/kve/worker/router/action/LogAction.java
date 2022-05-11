package com.kve.worker.router.action;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.worker.log.FileAppender;

import java.util.Date;

public class LogAction implements ExecutorAction{
    @Override
    public ResponseModel execute(RequestModel requestModel) {
        String logContent = FileAppender.readLog(new Date(requestModel.getLogDateTim()), requestModel.getScheduleLogId());

        return new ResponseModel(ResponseModel.SUCCESS, logContent);
    }
}
