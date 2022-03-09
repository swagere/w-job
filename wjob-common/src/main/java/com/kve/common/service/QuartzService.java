package com.kve.common.service;

import org.quartz.SchedulerException;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

public interface QuartzService {
    void resumeJob(String jobKey) throws SchedulerException;
}
