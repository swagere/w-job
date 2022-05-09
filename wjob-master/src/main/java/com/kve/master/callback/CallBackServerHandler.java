package com.kve.master.callback;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CallBackServerHandler extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String requestHex = request.getParameter(NetConnectionUtil.HEX);
        RequestModel requestModel = NetConnectionUtil.parseHexJson2Obj(requestHex, RequestModel.class);

        ResponseModel responseModel = null;

        //日志处理
    }
}
