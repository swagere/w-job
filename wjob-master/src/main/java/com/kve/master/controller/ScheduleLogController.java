package com.kve.master.controller;

import com.kve.master.config.response.AjaxResponse;
import com.kve.master.model.param.ScheduleLogPageParam;
import com.kve.master.model.vo.ScheduleLogPageVO;
import com.kve.master.service.ScheduleLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/job-admin/schedule-log")
public class ScheduleLogController {
    @Autowired
    ScheduleLogService scheduleLogService;
    /**
     * 分页日志列表
     */
    @RequestMapping("/listPage")
    public AjaxResponse listPage(ScheduleLogPageParam scheduleLogPageParam) {
        ScheduleLogPageVO result = scheduleLogService.listPageLog(scheduleLogPageParam);
        AjaxResponse response = AjaxResponse.success(result.getList());
        response.setCount(result.getTotal());
        return response;
    }
}
