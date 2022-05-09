package com.kve.worker.router.action;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;

public class BeatAction implements ExecutorAction {
    @Override
    public ResponseModel execute(RequestModel requestModel) {
        return new ResponseModel(ResponseModel.SUCCESS, "alive");
    }

}
