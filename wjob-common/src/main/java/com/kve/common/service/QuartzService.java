package com.kve.common.service;

import com.kve.common.bean.TaskEntity;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public interface QuartzService {
//    void resumeJob(String jobKey);
    void startJob(TaskEntity taskEntity) throws Exception;
}
