package com.kve.master.bean.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录参数
 *
 */
@Data
public class LoginParam implements Serializable {

    private static final long serialVersionUID = 4220789951474952467L;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码,需要加密
     */
    private String password;

}
