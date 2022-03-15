package com.kve.master.controller;

import com.kve.common.bean.TaskParam;
import com.kve.master.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    /**
     * 创建任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/createJob")
    @ResponseBody
    public Boolean createJob(@RequestBody TaskParam taskParam) {
        try {
            taskService.saveTask(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 启动任务
     *
     * @param taskParam
     * @return
     */
    @RequestMapping("/startJob")
    @ResponseBody
    public Boolean startJob(@RequestBody TaskParam taskParam) {
        try {
            taskService.startJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 恢复定时任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/resumeJob")
    @ResponseBody
    private Boolean resumeJob(@RequestBody TaskParam taskParam){
        try {
            taskService.resumeJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 暂停任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/pauseJob")
    @ResponseBody
    private Boolean pauseJob(@RequestBody TaskParam taskParam){
        try {
            taskService.pauseJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
