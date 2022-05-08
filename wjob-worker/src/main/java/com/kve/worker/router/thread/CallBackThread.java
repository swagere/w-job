package com.kve.worker.router.thread;

import com.kve.common.model.RequestModel;
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
                    // TODO: 2022/5/8 向调度中心发消息
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushCallBask(RequestModel callBack) {
        callBackQueue.add(callBack);
        log.info("[ CallBackThread ] push callBack request");
    }
}
