package com.kve.master.controller;


import com.kve.master.model.param.OperateLogDetailParam;
import com.kve.master.model.param.OperateLogPageParam;
import com.kve.master.model.vo.OperateLogPageVO;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务日志controller
 */
@RestController
@RequestMapping("/job-admin/job-log")
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;

    /**
     * 分页日志列表
     */
    @RequestMapping("/listPage")
    public AjaxResponse listPage(OperateLogPageParam operateLogPageParam) {
        OperateLogPageVO result = operateLogService.listPageLog(operateLogPageParam);
        AjaxResponse response = AjaxResponse.success(result.getList());
        response.setCount(result.getTotal());
        return response;
    }

    /**
     * 日志详情
     */
    @RequestMapping("/getLogDetail")
    public AjaxResponse getJobDetail(@RequestBody OperateLogDetailParam operateLogDetailParam) {
        return AjaxResponse.success(operateLogService.getLogDetail(operateLogDetailParam));
    }

}
