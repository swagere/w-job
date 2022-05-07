package com.kve.master.controller;

import com.kve.master.model.bean.TaskInfo;
import com.kve.master.model.base.BaseParam;
import com.kve.master.model.param.TaskPageParam;
import com.kve.master.model.param.TaskParam;
import com.kve.master.model.vo.TaskPageVO;
import com.kve.master.model.vo.UserInfoDetailVO;
import com.kve.master.config.exception.WJobException;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.config.response.SysExceptionEnum;
import com.kve.master.service.TaskService;
import com.kve.master.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

/**
 * @author: hujing39
 * @date: 2022-03-07
 */

@RestController
@RequestMapping("/job-admin/task")
public class TaskInfoController {
    /**
     * cookie_key
     */
    public static final String COOKIE_USER_INFO = "COOKIE_USER_INFO";

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    /**
     * 创建任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/createJob")
    @ResponseBody
    public AjaxResponse createJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.saveTask(taskParam);
        return AjaxResponse.success();
    }

    /**
     * 修改任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/updateJob")
    @ResponseBody
    public AjaxResponse updateJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.updateJob(taskParam);
        return AjaxResponse.success();
    }

    /**
     * 删除任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/deleteJob")
    @ResponseBody
    public AjaxResponse deleteJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.deleteJob(taskParam);
        return AjaxResponse.success();
    }

    /**
     * 任务列表
     * @param taskPageParam
     * @return
     */
    @RequestMapping("/listPage")
    @ResponseBody
    public AjaxResponse listPageTask(TaskPageParam taskPageParam) throws Exception {
        TaskPageVO taskPageVO = taskService.listPageTask(taskPageParam);
        AjaxResponse ajaxResponse = AjaxResponse.success(taskPageVO.getList());
        ajaxResponse.setCount(taskPageVO.getTotal());
        return ajaxResponse;
    }

    /**
     * 任务详情
     * @param taskParam
     * @return
     */
    @RequestMapping("/getJobDetail")
    @ResponseBody
    public AjaxResponse getJob(@RequestBody TaskParam taskParam) throws Exception {
        TaskInfo taskInfo = taskService.getJobDetail(taskParam);
        return AjaxResponse.success(taskInfo);
    }

    /**
     * 启动任务
     *
     * @param taskParam
     * @return
     */
    @RequestMapping("/startJob")
    @ResponseBody
    public AjaxResponse startJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.startJob(taskParam);
        return AjaxResponse.success();
    }

    /**
     * 暂停任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/pauseJob")
    @ResponseBody
    private AjaxResponse pauseJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.pauseJob(taskParam);
        return AjaxResponse.success();
    }

    /**
     * 恢复任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/resumeJob")
    @ResponseBody
    private AjaxResponse resumeJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.resumeJob(taskParam);
        return AjaxResponse.success();
    }

    /**
     * 恢复任务
     * @param taskParam
     * @return
     */
    @RequestMapping("/stopJob")
    @ResponseBody
    private AjaxResponse stopJob(@RequestBody TaskParam taskParam, HttpServletRequest request) throws Exception {
        buildOperate(taskParam, request);
        taskService.stopJob(taskParam);
        return AjaxResponse.success();
    }

    private void buildOperate(BaseParam baseParam, HttpServletRequest request) {
        //当前登录用户
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }

        UserInfoDetailVO currentUser = userService.getUserDetailByUsername(username);
        if (null != currentUser) {
            baseParam.setOperateBy(String.valueOf(currentUser.getId()));
            baseParam.setOperateName(currentUser.getUsername());
        }
        else {
            throw new WJobException(SysExceptionEnum.USER_NOT_EXIST);
        }
    }
}
