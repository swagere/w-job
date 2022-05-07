package com.kve.master.rpc;

import com.kve.master.bean.TaskInfo;
import com.kve.master.util.PropertyRead;
import org.quartz.ee.jmx.jboss.QuartzService;

import java.util.ArrayList;
import java.util.List;


//@Service
public class RpcService {
    private static List<QuartzService> workerList = new ArrayList<QuartzService>();

    static {
        //初始化
        String hostList = PropertyRead.getKey("hostList");
        String[] hosts = hostList.split(",");
        for (String host : hosts) {
//            QuartzService service = RpcClient.getRemoteProxyObj(QuartzService.class,
//                    new InetSocketAddress(host, 9000));
//            workerList.add(service);
        }
    }

    public static void startJob(TaskInfo taskInfo) throws Exception {
        for (QuartzService worker : workerList) {
//            worker.startJob(taskInfo); //调用远程服务
        }
    }

    public static void pauseJob(TaskInfo taskInfo) throws Exception {
        for (QuartzService worker : workerList) {
//            worker.pauseJob(taskInfo);
        }
    }

    public static void resumeJob(TaskInfo taskInfo) throws Exception {
        for (QuartzService worker : workerList) {
//            worker.startJob(taskInfo);
        }
    }
}
