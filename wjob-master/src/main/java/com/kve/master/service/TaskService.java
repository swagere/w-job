package com.kve.master.service;

import com.kve.master.bean.TaskEntity;
import com.kve.master.bean.param.TaskPageParam;
import com.kve.master.bean.param.TaskParam;
import com.kve.master.bean.vo.TaskPageVO;

public interface TaskService {

    void saveTask(TaskParam taskParam) throws Exception;

    void updateJob(TaskParam taskParam) throws Exception;

    void deleteJob(TaskParam taskParam) throws Exception;

    TaskPageVO listPageTask(TaskPageParam taskParam) throws Exception;

    TaskEntity getJobDetail(TaskParam taskParam) throws Exception;

    void startJob(TaskParam taskParam) throws Exception;

    void pauseJob(TaskParam taskParam) throws Exception;

    void resumeJob(TaskParam taskParam) throws Exception;

    void stopJob(TaskParam taskParam) throws Exception;


}
