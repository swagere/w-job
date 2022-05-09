package com.kve.worker.router.thread;

import com.kve.common.model.RequestModel;
import com.kve.common.model.ResponseModel;
import com.kve.common.util.NetConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class CallBackThread extends Thread {
    private LinkedBlockingQueue<RequestModel> callBackQueue;

    public CallBackThread() {
        this.callBackQueue = new LinkedBlockingQueue<RequestModel>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                RequestModel callBack = callBackQueue.take();
                if (callBack != null) {
                    //向调度中心发消息
                    try {
                        ResponseModel responseModel = NetConnectionUtil.postHex(NetConnectionUtil.addressToUrl(callBack.getMasterAddress()), callBack);
                        log.info("[ CallBackThread ] callback, RequestModel:{}, ResponseModel:{}", callBack, responseModel);
                    } catch (Exception e) {
                        log.info("[ CallBackThread ] callback exception:", e);
                    }
                }
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

    public void pushCallBask(RequestModel callBack) {
        callBackQueue.add(callBack);
        log.info("[ CallBackThread ] push callBack request");
    }
}
