package com.kve.master.controller;

import com.kve.master.bean.param.TaskPageParam;
import com.kve.master.bean.param.TaskParam;
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
     * 修改任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/updateJob")
    @ResponseBody
    public Boolean updateJob(@RequestBody TaskParam taskParam) {
        try {
            taskService.updateJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/deleteJob")
    @ResponseBody
    public Boolean deleteJob(@RequestBody TaskParam taskParam) {
        try {
            taskService.deleteJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 任务列表
     * @param taskPageParam
     * @return
     */
    @RequestMapping("/listPage")
    @ResponseBody
    public Boolean listPageTask(@RequestBody TaskPageParam taskPageParam) {
        try {
            taskService.listPageTask(taskPageParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 任务详情
     * @param taskParam
     * @return
     */
    @RequestMapping("/getJob")
    @ResponseBody
    public Boolean getJob(@RequestBody TaskParam taskParam) {
        try {
            taskService.getJobDetail(taskParam);
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

    /**
     * 恢复任务
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
     * 恢复任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/stopJob")
    @ResponseBody
    private Boolean stopJob(@RequestBody TaskParam taskParam){
        try {
            taskService.stopJob(taskParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
