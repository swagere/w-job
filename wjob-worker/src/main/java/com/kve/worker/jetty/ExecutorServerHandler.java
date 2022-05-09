package com.kve.worker.jetty;

import com.kve.worker.router.HandlerRouter;
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
public class ExecutorServerHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        httpServletRequest.setCharacterEncoding("UTF-8");
        httpServletResponse.setCharacterEncoding("UTF-8");

        // parse hex-json to request model
        String requestHex = httpServletRequest.getParameter(NetConnectionUtil.HEX);
        ResponseModel responseModel = null;
        if (requestHex!=null && requestHex.trim().length()>0) {
            try {
                // route trigger
                RequestModel requestModel = NetConnectionUtil.parseHexJson2Obj(requestHex, RequestModel.class);
                responseModel = HandlerRouter.route(requestModel);
            } catch (Exception e) {
                log.error("", e);
                responseModel = new ResponseModel(ResponseModel.SUCCESS, e.getMessage());
            }
        }
        if (responseModel == null) {
            responseModel = new ResponseModel(ResponseModel.SUCCESS, "系统异常");
        }

        // format response model to hex-json
        String responseHex = NetConnectionUtil.formatObj2HexJson(responseModel);

        // return
        httpServletResponse.setContentType("text/plain;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        httpServletResponse.getWriter().println(responseHex);
    }
}
