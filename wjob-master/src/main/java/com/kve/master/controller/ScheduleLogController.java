package com.kve.master.controller;

import com.kve.common.model.ActionEnum;
import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import com.kve.master.config.exception.WJobException;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.config.response.SysExceptionEnum;
import com.kve.master.mapper.ScheduleLogMapper;
import com.kve.master.model.bean.ScheduleLog;
import com.kve.master.model.param.LogParam;
import com.kve.master.model.param.ScheduleLogPageParam;
import com.kve.master.model.vo.ScheduleLogPageVO;
import com.kve.master.service.ScheduleLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/job-admin/schedule-log")
public class ScheduleLogController {
    @Autowired
    ScheduleLogService scheduleLogService;

    @Autowired
    ScheduleLogMapper scheduleLogMapper;

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

    @RequestMapping("/logDetail")
    @ResponseBody
    public AjaxResponse logDetail(@RequestBody LogParam logParam) {
        ScheduleLog log = scheduleLogMapper.findById(logParam.getId());
        if (log == null) {
            throw new WJobException(SysExceptionEnum.INVALID_PARAM);
        }
        if (!(ResponseModel.SUCCESS.equals(log.getTriggerStatus()) || !StringUtils.isEmpty(log.getExecuteStatus()))) {
            throw new WJobException(SysExceptionEnum.FAIL_JOB_CREATE);
        }

        // trigger id, trigger time
        RequestModel requestModel = new RequestModel();
        requestModel.setTimestamp(System.currentTimeMillis());
        requestModel.setAction(ActionEnum.LOG.getValue());
        requestModel.setScheduleLogId(logParam.getId());
        requestModel.setLogDateTim(log.getTriggerTime().getTime());

        ResponseModel responseModel = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(log.getExecutorAddress()), requestModel);
        if (ResponseModel.SUCCESS.equals(responseModel.getStatus())) {
            return AjaxResponse.success(responseModel.getMsg());
        } else {
            return AjaxResponse.error(500, "查看执行日志失败: " + responseModel.getMsg());
        }

    }
}
