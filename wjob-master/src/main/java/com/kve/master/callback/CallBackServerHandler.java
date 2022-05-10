package com.kve.master.callback;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import com.kve.master.mapper.ScheduleLogMapper;
import com.kve.master.model.bean.ScheduleLog;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class CallBackServerHandler extends AbstractHandler {
    @Autowired
    ScheduleLogMapper scheduleLogMapper;

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String requestHex = request.getParameter(NetConnectionUtil.HEX);
        RequestModel requestModel = NetConnectionUtil.parseHexJson2Obj(requestHex, RequestModel.class);

        ResponseModel responseModel = null;

        //日志处理
        ScheduleLog log = scheduleLogMapper.findById(requestModel.getScheduleLogId());

        if (log != null) {
            if (requestModel.getStatus().equals(ResponseModel.SUCCESS) && log.getExecuteStatus().equals(ResponseModel.SUCCESS)) {
                //启动子任务
            }

            //调度日志保存
            log.setExecuteTime(new Date());
            log.setExecuteStatus(requestModel.getStatus());
            log.setExecuteMsg(requestModel.getMsg());
            scheduleLogMapper.updateById(log);

            //表示日志更新成功（任务是否执行成功只表现在日志和页面中，不返回错误
            responseModel = new ResponseModel(ResponseModel.SUCCESS, null);
        } else {
            responseModel = new ResponseModel(ResponseModel.FAIL, "log not found");
        }
    }
}
