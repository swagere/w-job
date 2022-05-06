package com.kve.master.bean.param;

import com.kve.master.bean.base.BaseParam;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateParam extends BaseParam {

    /**
     * ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态 1-启用;2-停用;3-锁定
     */
    private Integer userStatus;

    /**
     * 用户类型 1-超级管理员 2-管理员 3-普通用户
     */
    private Integer userType;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 用户名
     */
    private String newPassword;

    /**
     * 菜单key 集合
     */
    private List<String> menus;

    /**
     * 功能key集
     */
    private List<String> functions;
}
