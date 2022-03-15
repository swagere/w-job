package com.kve.common.bean.base;

import lombok.Data;

import java.util.Date;

/**
 * @author: hujing39
 * @date: 2022-03-15
 */

@Data
public class BaseEntity {
    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最新修改人
     */
    private String updateBy;

    /**
     * 最新修改人名称
     */
    private String updateName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标记
     */
    private Integer delFlag;
}
