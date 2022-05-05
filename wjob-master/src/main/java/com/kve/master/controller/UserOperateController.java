package com.kve.master.controller;

import com.kve.master.bean.param.UserPageParam;
import com.kve.master.bean.param.UserParam;
import com.kve.master.bean.param.UserUpdateParam;
import com.kve.master.bean.vo.UserInfoPageVO;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

@Slf4j
@RestController
@RequestMapping("/job-admin/user")
public class UserOperateController {
    /**
     * cookie_key
     */
    public static final String COOKIE_USER_INFO = "COOKIE_USER_INFO";

    @Autowired
    UserService userService;

    /**
     * 用户详情
     *
     * @return 用户详情
     * @author mengq
     */
    @RequestMapping("/getUserPersonDetail")
    public AjaxResponse getUserPersonDetail(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }
        return AjaxResponse.success(userService.getUserPersonDetail(username));
    }

    /**
     * 分页列表
     *
     */
    @RequestMapping("/listPage")
    public AjaxResponse listPageJob(UserPageParam userPageParam, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }

        UserInfoPageVO result = userService.listPage(userPageParam, username);
        AjaxResponse response = AjaxResponse.success(result.getList());
        response.setCount(result.getTotal());
        return response;
    }

    /**
     * 用户详情
     *
     */
    @RequestMapping("/getUserDetail")
    public AjaxResponse getUserDetail(@RequestBody UserParam userParam) {
        return AjaxResponse.success(userService.getUserDetail(userParam));
    }

    /**
     * 修改用户
     */
    @RequestMapping("/update")
    public AjaxResponse updateUser(@RequestBody UserUpdateParam userUpdateParam, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }

        userService.updateUser(userUpdateParam, username);
        return AjaxResponse.success();
    }

    /**
     * 修改密码
     */
    @RequestMapping("/update-pwd")
    public AjaxResponse updatePwd(@RequestBody UserUpdateParam userUpdateParam, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }

        userService.updatePwd(userUpdateParam, username);
        return AjaxResponse.success();
    }

    /**
     * 修改权限
     */
    @RequestMapping("/update-power")
    public AjaxResponse updatePower(@RequestBody UserUpdateParam userUpdateParam, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }

        userService.updatePwd(userUpdateParam, username);

        userService.updateUserPower(userUpdateParam, username);
        return AjaxResponse.success();
    }

    /**
     * 删除用户
     */
    @RequestMapping("/delete")
    public AjaxResponse deleteUser(@RequestBody UserUpdateParam userUpdateParam, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }

        userService.deleteUser(userUpdateParam, username);
        return AjaxResponse.success();
    }
}
