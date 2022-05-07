package com.kve.master.controller;


import com.kve.master.model.param.LogDetailParam;
import com.kve.master.model.param.LogPageParam;
import com.kve.master.model.vo.LogPageVO;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务日志controller
 */
@RestController
@RequestMapping("/job-admin/job-log")
public class LogInfoController {

    @Autowired
    private LogService logService;

    /**
     * 分页任务列表
     */
    @RequestMapping("/listPage")
    public AjaxResponse listPageJob(LogPageParam logPageParam) {
        LogPageVO result = logService.listPageLog(logPageParam);
        AjaxResponse response = AjaxResponse.success(result.getList());
        response.setCount(result.getTotal());
        return response;
    }

    /**
     * 日志详情
     */
    @RequestMapping("/getLogDetail")
    public AjaxResponse getJobDetail(@RequestBody LogDetailParam logDetailParam) {
        return AjaxResponse.success(logService.getLogDetail(logDetailParam));
    }

}
