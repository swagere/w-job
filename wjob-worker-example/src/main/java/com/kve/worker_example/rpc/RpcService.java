package com.kve.worker_example.rpc;//package com.kve.worker.rpc;
//
//import com.kve.common.model.RequestModel;
//import com.kve.common.model.ResponseModel;
//import com.kve.master.util.PropertyRead;
//import org.springframework.stereotype.Service;
//
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Service
//public class RpcService {
//    private static List<RpcHandler> workerList = new ArrayList<RpcHandler>();
//
//    static {
//        //初始化
//        String hostList = PropertyRead.getKey("hostList");
//        String[] hosts = hostList.split(",");
//        for (String host : hosts) {
//            RpcHandler service = RpcClient.getRemoteProxyObj(RpcHandler.class,
//                    new InetSocketAddress(host, 9000));
//            workerList.add(service);
//        }
//    }
//
//    public ResponseModel sendMessage(RequestModel requestModel) throws Exception {
//        ResponseModel responseModel = null;
//        for (RpcHandler worker : workerList) {
//            responseModel = worker.accept(requestModel); //调用远程服务
//        }
//        return responseModel;
//    }
//}
