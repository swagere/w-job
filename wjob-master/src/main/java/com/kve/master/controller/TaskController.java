package com.kve.master.controller;

import com.alibaba.fastjson.JSON;
import com.kve.common.rpc.RpcClient;
import com.kve.common.service.QuartzService;
import com.kve.master.service.TaskService;
import com.kve.common.util.PropertyRead;
import com.kve.master.model.TaskParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

@RestController
@RequestMapping("/task")
public class TaskController {
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

    @Autowired
    TaskService taskService;

    @RequestMapping("/deleteJob")
    @ResponseBody
    private Boolean deleteJob(@RequestBody String str) {
        String jobKey = JSON.parseObject(str).get("jobKey").toString();
        String[] keyArray = jobKey.split("\\.");

        try {
            taskService.deleteJob(keyArray[1], keyArray[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @RequestMapping("/list")
    @ResponseBody
    public Boolean toList() {
        List<HashMap<String, Object>> jobList = null;
        try {
            jobList = taskService.list();
            System.out.println(jobList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    @RequestMapping("/addJob")
    @ResponseBody
    public Boolean addJob(@RequestBody TaskParam taskParam) {
        try {
            taskService.createJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 执行定时任务
     * @param str
     * @return
     */
    @RequestMapping("/resumeJob")
    @ResponseBody
    private Boolean resumeJob(@RequestBody String str){
        String jobKey = JSON.parseObject(str).get("jobKey").toString();

        try {
            for (QuartzService worker : workerList) {
                 worker.resumeJob(jobKey); //调用远程服务
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
