package com.kve.master.rpc;

import com.kve.master.bean.TaskEntity;
import com.kve.master.rpc.RpcClient;
import com.kve.master.util.PropertyRead;
import org.quartz.ee.jmx.jboss.QuartzService;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: hujing39
 * @date: 2022-03-16
 */

@Service
public class RpcService {
    private static List<QuartzService> workerList = new ArrayList<QuartzService>();

    static {
        //初始化
        String hostList = PropertyRead.getKey("hostList");
        String[] hosts = hostList.split(",");
        for (String host : hosts) {
            QuartzService service = RpcClient.getRemoteProxyObj(QuartzService.class,
                    new InetSocketAddress(host, 9000));
            workerList.add(service);
        }
    }

    public static void startJob(TaskEntity taskEntity) throws Exception {
        for (QuartzService worker : workerList) {
//            worker.startJob(taskEntity); //调用远程服务
        }
    }

    public static void pauseJob(TaskEntity taskEntity) throws Exception {
        for (QuartzService worker : workerList) {
//            worker.pauseJob(taskEntity);
        }
    }

    public static void resumeJob(TaskEntity taskEntity) throws Exception {
        for (QuartzService worker : workerList) {
//            worker.startJob(taskEntity);
        }
    }
}
