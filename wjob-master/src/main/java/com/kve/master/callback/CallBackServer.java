package com.kve.master.callback;

import com.kve.common.config.ApplicationContextHelper;
import com.kve.master.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CallBackServer {
    private static int callBackPort;
    private static String ip;
    private static String address;

    Server server = null;

    static {
        callBackPort = 8888;
        ip = IpUtil.getIp();
        address = ip.concat(":").concat(String.valueOf(callBackPort));
    }

    public String getAddress() {
        return address;
    }

    @PostConstruct
    public void init() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPool threadPool = new ExecutorThreadPool(new ThreadPoolExecutor(200, 200, 30000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));	// 非阻塞
                server = new Server();
                server.setThreadPool(threadPool);

                SelectChannelConnector connector = new SelectChannelConnector();
                connector.setPort(callBackPort);
                connector.setMaxIdleTime(30000);
                server.setConnectors(new Connector[] { connector });

                HandlerCollection handlerCollection = new HandlerCollection();
                handlerCollection.setHandlers(new Handler[]{ApplicationContextHelper.getBean(CallBackServerHandler.class)});
                server.setHandler(handlerCollection);

                try {
                    server.start();
                    log.info("[ CallBackServer ] jetty server start success at port:{}." , callBackPort);
                    server.join();
                    log.info("[ CallBackServer ] jetty server join success at port:{}." , callBackPort);
                } catch (Exception e) {
                    log.error("[ CallBackServer ] jetty start exception at port:{}, e:{}" , callBackPort, e);
                }
            }
        }).start();
    }

    @PreDestroy
    public void destroy() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                log.error("[ CallBackServer ] jetty destroy exception at port:{}, e:{}" , callBackPort, e);
            }
        }
    }
}
