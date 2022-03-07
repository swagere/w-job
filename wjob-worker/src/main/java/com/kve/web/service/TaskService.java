package com.kve.web.service;

import org.quartz.SchedulerException;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public interface TaskService {
    void resumeJob(String jobKey) throws SchedulerException;
}
