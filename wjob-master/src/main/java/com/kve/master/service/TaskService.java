package com.kve.master.service;

import com.kve.master.model.bean.TaskInfo;
import com.kve.master.model.param.TaskPageParam;
import com.kve.master.model.param.TaskParam;
import com.kve.master.model.vo.TaskPageVO;

public interface TaskService {

    void saveTask(TaskParam taskParam) throws Exception;

    void updateJob(TaskParam taskParam) throws Exception;

    void deleteJob(TaskParam taskParam) throws Exception;

    TaskPageVO listPageTask(TaskPageParam taskParam) throws Exception;

    TaskInfo getJobDetail(TaskParam taskParam) throws Exception;

    void startJob(TaskParam taskParam) throws Exception;

    void pauseJob(TaskParam taskParam) throws Exception;

    void resumeJob(TaskParam taskParam) throws Exception;

    void stopJob(TaskParam taskParam) throws Exception;


}
