package com.kve.common.service;

import com.kve.common.bean.TaskEntity;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public interface QuartzService {
    /**
     * 运行/恢复 任务
     */
    void startJob(TaskEntity taskEntity) throws Exception;

    /**
     * 暂停任务
     */
    void pauseJob(TaskEntity taskEntity) throws Exception;
}
