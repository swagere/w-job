package com.kve.master.bean.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mengq
 * @version 1.0
 */
@Data
public class BaseOperate implements Serializable {

    private static final long serialVersionUID = -4793987603724560341L;

    /**
     * 操作人ID
     */
    private String operateBy;

    /**
     * 操作人名称
     */
    private String operateName;

    /**
     * 登录IP
     */
    private String clientIp;

    /**
     * 登录IP实际地理位置
     */
    private String ipAddress;

    /**
     * 浏览器信息
     */
    private String browserName;

    /**
     * 系统信息
     */
    private String os;

}
