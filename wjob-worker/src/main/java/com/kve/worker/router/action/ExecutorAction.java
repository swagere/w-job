package com.kve.worker.router.action;


import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;

public interface ExecutorAction {

    public abstract ResponseModel execute(RequestModel requestModel);

}
