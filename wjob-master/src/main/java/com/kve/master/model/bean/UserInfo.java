package com.kve.master.model.bean;


import com.kve.master.model.base.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

@Data
@Slf4j
public class UserInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3437989904588702812L;

    /**
     * 配置ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户类型 1-超级管理员 2-管理员 3-普通用户
     */
    private Integer userType;

    /**
     * 状态 1-启用;2-停用;3-锁定
     */
    private Integer userStatus;

    /**
     * 菜单集
     */
    private String menus;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 功能集
     */
    private String functions;

    public String getCookieValue() {
        String cookieValue = "userId={0};username={1};userType={2};menus={3};functions={4}";
        return MessageFormat.format(cookieValue, id, username, userType, menus, functions);
    }

    public String getUrlEncoderCookieValue() {
        try {
            return URLEncoder.encode(this.getCookieValue(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("cookie URLEncoder exception ", e);
        }
        return "";
    }

    public String getLogInfo() {
        String strValue = "userId: {0} , username: {1}";
        return MessageFormat.format(strValue, id, username);
    }
}
