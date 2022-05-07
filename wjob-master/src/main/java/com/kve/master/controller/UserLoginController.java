package com.kve.master.controller;

import com.alibaba.fastjson.JSON;
import com.kve.master.model.bean.UserInfo;
import com.kve.master.model.param.LoginParam;
import com.kve.master.config.response.AjaxResponse;
import com.kve.master.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录
 *
 */
@Slf4j
@RestController
@RequestMapping("/job-admin/login")
public class UserLoginController {

    /**
     * cookie_key
     */
    public static final String COOKIE_USER_INFO = "COOKIE_USER_INFO";

    @Autowired
    UserServiceImpl userService;

    /**
     * 用户登录
     *
     * @param loginParam param
     * @param response   param
     * @return 登录用户信息
     */
    @PostMapping("/in")
    public AjaxResponse loginIn(@RequestBody LoginParam loginParam, HttpServletResponse response) throws Exception {
        log.info("[ LoginController ] >> 用户登录 user:{}", JSON.toJSONString(loginParam));

        UserInfo user = userService.login(loginParam);

        //设置Cookie
        Cookie cookie = new Cookie(COOKIE_USER_INFO, user.getUrlEncoderCookieValue());
        //120分钟
        cookie.setMaxAge(120 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
        return AjaxResponse.success();
    }

    /**
     * 用户登出
     *
     * @param request  req
     * @param response res
     * @return 登出
     */
    @RequestMapping("/out")
    public AjaxResponse loginOut(HttpServletRequest request, HttpServletResponse response) {
        log.info("[ LoginController ] >> 用户登出");
        Cookie[] cookies = request.getCookies();
        //清除cookie
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                cookie.setValue(null);
                //立即销毁cookie
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                break;
            }
        }
        return AjaxResponse.success();
    }

    /**
     * 获取登录用户信息
     *
     * @return 登录用户信息
     */
    @RequestMapping("/getUserInfo")
    public AjaxResponse getUserInfo(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(2);
        //当前登录用户
        Cookie[] cookies = request.getCookies();
        String username = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_INFO)) {
                username = URLDecoder.decode(cookie.getValue()).split(";")[1].substring(9);
                break;
            }
        }
        map.put("currentUser", userService.getUserDetailByUsername(username));
        //权限信息
//        map.put("permissions", JobAdminShiroOperation.getPermissions());
        return AjaxResponse.success(map);
    }

}
