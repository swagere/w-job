package com.kve.worker.jetty;

import com.kve.common.config.ApplicationContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
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

@Component
@Slf4j
public class ExecutorServer {
    private static int port = 9999;

    Server server = null;

    @PostConstruct
    public void init() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPool threadPool = new ExecutorThreadPool(new ThreadPoolExecutor(200, 200, 30000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));	// 非阻塞
                server = new Server();
                server.setThreadPool(threadPool);

                SelectChannelConnector connector = new SelectChannelConnector();
                connector.setPort(port);
                connector.setMaxIdleTime(30000);
                server.setConnectors(new Connector[] { connector });

                HandlerCollection handlerCollection = new HandlerCollection();
                handlerCollection.setHandlers(new Handler[]{ApplicationContextHelper.getBean(ExecutorServerHandler.class)});
                server.setHandler(handlerCollection);

                try {
                    server.start();
                    log.info("[ ExecutorServer ] jetty server start success at port:{}." , port);
                    server.join();
                    log.info("[ ExecutorServer ] jetty server join success at port:{}." , port);
                } catch (Exception e) {
                    log.error("[ ExecutorServer ] jetty start exception at port:{}, e:{}" , port, e);
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
                log.error("[ ExecutorServer ] jetty destroy exception at port:{}, e:{}" , port, e);
            }
        }
    }
}
