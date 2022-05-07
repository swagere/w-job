package com.kve.master.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfoDetailVO implements Serializable {

    private static final long serialVersionUID = 8644475177314757145L;

    /**
     * ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态 1-启用，2-停用
     */
    private Integer userStatus;
    /**
     * 状态 1-启用，2-停用
     */
    private String userStatusStr;

    /**
     * 用户类型 1-超级管理员 2-管理员 3-普通用户
     */
    private Integer userType;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 菜单集
     */
    private String menus;

    /**
     * 功能集
     */
    private String functions;

    private String createBy;
    private String createName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String updateBy;
    private String updateName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

}