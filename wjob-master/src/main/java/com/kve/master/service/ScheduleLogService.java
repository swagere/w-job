package com.kve.master.service;

import com.kve.master.model.param.ScheduleLogPageParam;
import com.kve.master.model.vo.ScheduleLogPageVO;

public interface ScheduleLogService {
    ScheduleLogPageVO listPageLog(ScheduleLogPageParam scheduleLogPageParam);
}
